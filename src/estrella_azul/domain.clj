(ns estrella-azul.domain
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def PASSWORD (or (System/getenv "PASSWORD") "password"))

(defn- numero-de-archivo [filename]
  (parse-long (re-find #"\d+" filename)))

(defn- listar-slides []
  (let [dir (io/file "./slides")]
    (if (.isDirectory dir)
      (->> (into []
                 (comp
                  (filter #(.isFile %))
                  (map #(.getName %))
                  (filter #(str/ends-with? (str/lower-case %) ".svg"))
                  (map (fn [filename]
                         {:nombre-archivo filename
                          :archivo (slurp (str "./slides/" filename))
                          :tiene-estrella? (str/includes? filename "estrella")
                          :estrella-reclamada-por? nil})))
                 (.listFiles dir))
           (sort-by (comp numero-de-archivo :nombre-archivo))
           vec)
      [])))

(def estado-default
  (let [slides (listar-slides)]
    {:usuarios {}
     :numero-de-slide 0
     :slides slides}))

(defonce estado (atom estado-default))

(set-validator! estado
                (fn [nuevo-estado]
                  (let [{:keys [numero-de-slide slides]} nuevo-estado]
                    (and (>= numero-de-slide 0)
                         (<= numero-de-slide (count slides))))))

(defn is-password-correct? [password]
  (= password PASSWORD))

(defn crear-usuario [nombre]
  {:id (str (java.util.UUID/randomUUID))
   :nombre nombre
   :cantidad-de-estrellas 0})

(defn agregar-slide! [slide]
  (swap! estado update-in [:slides] into [slide]))

(defn agregar-usuario! [usuario]
  (swap! estado assoc-in [:usuarios (:id usuario)] usuario))

(defn crear-y-guardar-usuario! [& args]
  (let [nuevo-usuario (apply crear-usuario args)]
    (agregar-usuario! nuevo-usuario)
    nuevo-usuario))

(defn estrella-disponible? [slide]
  (boolean (and (:tiene-estrella? slide) (not (:estrella-reclamada-por? slide)))))

(defn- slide [estado]
  (let [slides (:slides estado)
        slide-actual (get slides (:numero-de-slide estado))]
    (assoc slide-actual
           :numero (:numero-de-slide estado)
           :estrella-disponible? (estrella-disponible? slide-actual) ;; no se usa hay-estrella? para no consultar dos estados diferentes
           :total-slides (count slides))))

(defn slide-actual []
  (slide @estado))

(defn- mover-slide! [direccion]
  (try
   (let [nuevo-estado (swap! estado update :numero-de-slide direccion)]
     (slide nuevo-estado))
   (catch IllegalStateException _#
     nil)))

(defn avanzar-slide! []
  (mover-slide! inc))

(defn retroceder-slide! []
  (mover-slide! dec))

(defn hay-estrella? []
  (let [slide (slide-actual)]
    (estrella-disponible? slide)))

(defn reclamar-estrella! [id-usuario]
  (let [numero (:numero-de-slide @estado)
        dueño #(get-in % [:slides numero :estrella-reclamada-por?])
        [antes después] (swap-vals! estado
                                   (fn [st]
                                     (let [slide-en-curso (get-in st [:slides numero])]
                                       (if (and (contains? (:usuarios st) id-usuario)
                                                (estrella-disponible? slide-en-curso))
                                         (-> st
                                             (assoc-in [:slides numero :estrella-reclamada-por?] id-usuario)
                                             (update-in [:usuarios id-usuario :cantidad-de-estrellas] inc))
                                         st))))]
    (and (nil? (dueño antes))
         (= id-usuario (dueño después)))))

(defn ranking []
  (->> (vals (:usuarios @estado))
       (sort-by :cantidad-de-estrellas >)
       (take 3)
       (into [])))
