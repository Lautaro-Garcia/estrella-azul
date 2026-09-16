(ns estrella-azul.controllers
  (:require [starfederation.datastar.clojure.api :as d*]
            [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open write-profile gzip-profile]]
            [ring.util.response :as response]
            [cheshire.core :as json]
            [estrella-azul.views :as views]
            [estrella-azul.domain :as d]))

(defonce conecciones (atom #{}))

(defonce admin (atom nil))

(defn broadcast-señales! [señales & {:keys [incluir-admin?] :or {incluir-admin? false}}]
  (let [señales-serializadas (json/generate-string señales)]
    (doseq [c @conecciones]
      (d*/patch-signals! c señales-serializadas))
    (when incluir-admin? (d*/patch-signals! @admin señales-serializadas))))

(defn broadcast-elemento! [elemento & {:keys [incluir-admin? opts] :or {incluir-admin? false opts {}}}]
  (doseq [c @conecciones]
    (d*/patch-elements! c elemento opts))
  (when incluir-admin? (d*/patch-elements! @admin elemento opts)))

(defn- page [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn- resetear-conecciones! []
  (reset! admin nil)
  (reset! conecciones #{})
  (reset! d/estado d/estado-default))

(defn login-admin [_request]
  (page (views/login-admin)))

(defn login-usuario [_request]
  (page (views/login-usuario)))

(defn podio [_request]
  (page (views/podio (d/ranking))))

(defn crear-usuario-nuevo [request]
  (->sse-response request
                  {on-open
                   (fn [sse]
                     (let [nombre ((:form-params request) "nombre")
                           nuevo-usuario (d/crear-y-guardar-usuario! nombre)]
                       (d*/redirect! sse  (str "/usuarios/" (:id nuevo-usuario)))))}))

(defn crear-admin-nuevo [request]
  (let [password ((:form-params request) "password")]
    (if (not (d/is-password-correct? password))
      (response/redirect "/admin")

      (do
       (when @admin
        (d*/close-sse! @admin))
       (->sse-response request
                       {write-profile gzip-profile
                        on-open
                        (fn [sse]
                          (reset! admin sse)
                          (d*/patch-elements! sse (views/slide (d/slide-actual)) {d*/selector "body"}))})))))

(defn reset-handler [_request]
  (resetear-conecciones!)
  (response/redirect "/admin"))

(defn reclamar-estrella [request]
  (->sse-response request
                  {on-open
                   (fn [sse]
                     (d*/with-open-sse sse
                       (let [señales (json/parse-string (slurp (d*/get-signals request)) true)
                             id-usuario (:id señales)]
                         (when (d/reclamar-estrella! id-usuario)
                           (broadcast-elemento! (views/notificacion-estrella) :incluir-admin? true :opts {d*/patch-mode d*/pm-append d*/selector "main"})
                           (broadcast-señales! {:estrellaDisponible false} :incluir-admin? true)
                           (Thread/sleep 3000)
                           (broadcast-elemento! (views/notificacion-estrella) :incluir-admin? true :opts {d*/patch-mode d*/pm-remove d*/selector ".notificacion-overlay"})))))}))

(defn- cambiar-slide [nueva-slide]
  (when-let [{:keys [numero estrella-disponible?]} nueva-slide]
    (broadcast-señales! {:numeroSlide numero :estrellaDisponible estrella-disponible?})
    (d*/patch-elements! @admin (views/slide nueva-slide) {d*/selector "body"}))
  {:status 200})

(defn siguiente-slide [_request]
  (cambiar-slide (d/avanzar-slide!)))

(defn anterior-slide [_request]
  (cambiar-slide (d/retroceder-slide!)))

(defn pagina-perfil-usuario [request]
  (let [id-usuario (get-in request [:path-params :id])
        usuario (get (:usuarios @d/estado) id-usuario)]

    (if (nil? usuario)
      (page (views/perfil-usuario-desconocido))
      (page (views/perfil-usuario usuario (d/hay-estrella?))))))

(defn perfil-usuario [request]
  (let [id-usuario (get-in request [:path-params :id])
        {:keys [estrella-disponible? numero]} (d/slide-actual)
        señales {:id id-usuario :numeroSlide numero :estrellaDisponible estrella-disponible?}]

    (->sse-response request
                    {on-open
                     (fn [sse]
                       (swap! conecciones conj sse)
                       (d*/patch-signals! sse (json/generate-string señales)))})))
