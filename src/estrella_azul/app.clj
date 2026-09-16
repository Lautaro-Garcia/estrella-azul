(ns estrella-azul.app
  (:require [org.httpkit.server :as http]
            [reitit.ring :as reitit-ring]
            [ring.middleware.params :refer [wrap-params]]
            [estrella-azul.controllers :as controllers]))
(def app
  (-> (reitit-ring/ring-handler
       (reitit-ring/router
        [["/reset" {:get #'controllers/reset-handler}]
         ["/admin" {:get #'controllers/login-admin :post #'controllers/crear-admin-nuevo}]
         ["/siguiente-slide" {:post #'controllers/siguiente-slide}]
         ["/anterior-slide" {:post #'controllers/anterior-slide}]
         ["/usuarios"
          ["" {:get  #'controllers/login-usuario :post #'controllers/crear-usuario-nuevo}]
          ["/:id" {:get #'controllers/pagina-perfil-usuario}]
          ["/:id/data" {:get #'controllers/perfil-usuario}]
          ["/:id/reclamar-estrella" {:post #'controllers/reclamar-estrella}]]
         ["/podio" {:get #'controllers/podio}]])
       (reitit-ring/create-default-handler))
      wrap-params))

(defonce server (atom nil))

(def PORT (or (some-> (System/getenv "PORT") parse-long) 3100))

(defn start-server!
  ([] (start-server! {:port PORT}))
  ([{:keys [port] :or {port PORT}}]
   (when-not @server
     (reset! server (http/run-server #'app {:port port}))
     (println "Estrella Azul started on http://localhost:" port))))

(defn stop-server! []
  (when @server
    (@server :timeout 100)
    (reset! server nil)
    (println "Estrella Azul stopped.")))
