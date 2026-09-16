(ns estrella-azul.utils
  (:require [starfederation.datastar.clojure.api :as d*]
            [cheshire.core :as json]))

(defprotocol ParseableSignal
  "Returns a clojure map of signals."
  (signals [this]))

(extend-protocol ParseableSignal
  clojure.lang.PersistentHashMap
  (signals [s] (signals (d*/get-signals s)))
  String
  (signals [s] (json/parse-string s true))
  org.httpkit.BytesInputStream
  (signals [s] (json/parse-string (slurp s) true)))
