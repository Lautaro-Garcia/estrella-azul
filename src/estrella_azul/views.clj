(ns estrella-azul.views
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.def :refer [defhtml]]
            [hiccup.core :refer [html]]
            [starfederation.datastar.clojure.api :as d*]
            [clojure.string :as str]
            [estrella-azul.styles :as estilos]))

(defn base-layout [page-styles & content]
  (html5
    {:lang "en"}
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
     [:title "Estrella Azul"]
     [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
     [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin "anonymous"}]
     (include-css "https://fonts.googleapis.com/css2?family=Darker+Grotesque:wght@400;600;700&family=Syncopate:wght@400;700&display=swap")
     [:script {:type "module"
               :src "https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.2/bundles/datastar.js"}]
     [:style estilos/base]
     (when page-styles [:style page-styles])]
    [:body
     [:div.viewport-frame
      (into [:main] content)]]))

(defn login-usuario []
  (base-layout estilos/login
    [:div.contenedor
     [:h1 "Bienvendix viajerx"]
     [:form {:data-on:submit (d*/sse-post "/usuarios" "{contentType: 'form'}")}
      [:div.input-group
       [:label {:for "nombre"} "Nombre"]
       [:input {:type "text" :id "nombre" :name "nombre" :required true :autofocus true}]]
      [:button.font-syncopate "Entrar"]]]))

(defn login-admin []
  (base-layout estilos/login
    [:div.contenedor
     [:h1 "Bienvenidx admin"]
     [:p "¿Qué dicen las estrellas?"]
     [:form {:data-on:submit (d*/sse-post "/admin" "{contentType: 'form'}")}
      [:div.input-group
       [:label {:for "password"} "Palabra clave"]
       [:input {:type "password" :id "password" :name "password" :required true :autofocus true}]]
      [:button.font-syncopate "Develar"]]]))


(defn icono-estrella []
  [:svg {:viewBox "49217 50839 11740 15426" :xmlns "http://www.w3.org/2000/svg"}
   [:g {:id "editor-g14d19ccce55_1_879"}
    [:g
     [:g]
     [:g
      [:path {:fill-opacity "1"
              :d "M 55079.4782 66265.2629 C 54582.5378 59747.7389 54161.9066 59215.8209 49217.6288 58542.0581 54161.9066 57887.8601 54582.5378 57336.3773 55079.4782 50839.6409 55593.1694 57336.3773 55997.0498 57887.8601 60957.1478 58542.0581 55997.0498 59215.8209 55593.1694 59747.7389 55079.4782 66265.2629 Z"}]]
     [:g
      [:path {:fill "none"
              :stroke "none"
              :stroke-width "5166.1016949152545"
              :stroke-linecap "round"
              :stroke-linejoin "round"
              :stroke-miterlimit "10"
              :pointer-events "visiblePainted"
              :d "M 55079.4782 66265.2629 C 54582.5378 59747.7389 54161.9066 59215.8209 49217.6288 58542.0581 54161.9066 57887.8601 54582.5378 57336.3773 55079.4782 50839.6409 55593.1694 57336.3773 55997.0498 57887.8601 60957.1478 58542.0581 55997.0498 59215.8209 55593.1694 59747.7389 55079.4782 66265.2629 Z"}]]]]])

(defn- avatar [usuario]
  [:img.avatar {:alt "avatar" :src (str "https://api.dicebear.com/10.x/constellation/svg?animationVariant=fastest&seed=" (:id usuario))}])

(defn perfil-usuario [usuario estrella-disponible?]
  (base-layout estilos/perfil-usuario
               [:div.contenedor {:data-init (d*/sse-get (str "/usuarios/" (:id usuario) "/data"))}
                [:div.header
                 [:div.font-syncopate (:nombre usuario)]
                 (avatar usuario)]

                [:div.centrado
                 [:div.numero-slide.font-syncopate {:data-text "$numeroSlide.toString().padStart(2, '0')"} "-"]
                 [:div.label "Número de slide"]]

                [:div.footer
                 [:button.estrella {:data-signals (str "{id: '" (:id usuario) "', estrellaDisponible: " (boolean estrella-disponible?) "}")
                                    :data-attr "{disabled: !$estrellaDisponible}"
                                    :data-on:click (str "$estrellaDisponible = false; "
                                                        (d*/sse-post (str "/usuarios/" (:id usuario) "/reclamar-estrella") "{filterSignals: {include: /id/}}"))
                                    :data-class "{azul: $estrellaDisponible, negro: !$estrellaDisponible}"
                                    :data-style:cursor "$estrellaDisponible && 'pointer'"
                                    :class (if estrella-disponible? "azul" "negro")}
                  (icono-estrella)]]]))

(defn perfil-usuario-desconocido []
  (base-layout estilos/perfil-usuario
               [:div.contenedor
                [:div.centrado
                 [:div.numero-slide.font-syncopate "?"]
                 [:div.label "Las estrellas no saben de tu paradero."]]

                [:div.footer
                 [:button.atras {:data-on:click (d*/sse-get "/usuarios")} "Volver"]]]))

(defhtml notificacion-estrella []
  [:div.notificacion-overlay.azul
   (icono-estrella)])

(def ^:private patrón-estrella-azul
  #"<path\b[^>]*?fill=\"#2847bd\"[^>]*?d=\"[mMcCzZ0-9eE\s.,+-]+?\"[^>]*?/>")

(defn- atributos-svg [etiqueta]
  (into {}
        (map (fn [[_ nombre valor]] [(keyword nombre) valor]))
        (re-seq #"([\w:-]+)=\"([^\"]*)\"" etiqueta)))

(defn- hacer-estrella-dinámica [svg disponible?]
  (str/replace svg patrón-estrella-azul
               (fn [etiqueta]
                 (html [:g {:class (if disponible? "azul" "negro")
                            :data-class "{negro: !$estrellaDisponible, azul: $estrellaDisponible}"}
                        [:path (assoc (atributos-svg etiqueta) :fill "var(--accent-blue)")]]))))

(defn- tecla [nombre]
  (str "evt.key == '" nombre "' && "))

(defn- anterior [slide-actual]
  (and (> slide-actual 0) (str (tecla "ArrowLeft") (d*/sse-post "/anterior-slide"))))

(defn- siguiente [slide-actual total-slides]
  (let [destino (if (< slide-actual total-slides)
                  (d*/sse-post "/siguiente-slide")
                  (d*/sse-get "/podio"))]
    (str (tecla "ArrowRight") destino)))

(defn slide [{:keys [estrella-disponible? tiene-estrella? archivo numero total-slides]}]
  (let [acciones (str/join " || " [(anterior numero) (siguiente numero total-slides)])
        svg (if tiene-estrella?
              (hacer-estrella-dinámica archivo estrella-disponible?)
              archivo)]
    (html
        [:body.full
         [:div {:data-signals (str "{estrellaDisponible: " (boolean estrella-disponible?) "}") :style "height: 100vh;"}
          [:main.svg-viewport {:data-on:keydown__window acciones}
           svg]]])))

(defn- columna-podio [posicion usuario]
  (when usuario
    [:div {:class (str "podio-col posicion-" posicion)}
     [:div.posicion.font-syncopate (format "%02d" posicion)]
     (avatar usuario)
     [:div.nombre.font-syncopate (:nombre usuario)]
     [:div.estrellas
      (icono-estrella)
      [:span (:cantidad-de-estrellas usuario)]]]))

(defn podio [[primero segundo tercero]]
  (let [accion-anterior (str (tecla "ArrowLeft") (d*/sse-get "/admin"))]
    (base-layout estilos/podio
      [:div.contenedor {:data-on:keydown__window accion-anterior}
       [:div.header
        [:div.font-syncopate "Podio Estelar"]
        [:div.label "Los estrellas estuvieron a su favor"]]
       [:div.podio-grid
        (columna-podio 2 segundo)
        (columna-podio 1 primero)
        (columna-podio 3 tercero)]])))
