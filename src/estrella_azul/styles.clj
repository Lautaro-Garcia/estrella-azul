(ns estrella-azul.styles
  (:require [garden.core :refer [css]]
            [garden.stylesheet :refer [at-keyframes]]))

(def base
  (css {:pretty-print? false}
    [":root" {:--bg-color "#fff5eb"
              :--text-color "#191919"
              :--line-color "#bdb6ae"
              :--accent-blue "#2847bd"}]

    [:body {:background-color "var(--bg-color)"
            :color "var(--text-color)"
            :font-family "'Darker Grotesque', sans-serif"
            :font-size "1.25rem"
            :line-height "1.5"
            :margin "0"
            :padding "0"}]

    [:body.full {:margin "0"
                 :padding "0"
                 :width "100vw"
                 :height "100svh"
                 :overflow "hidden"
                 :background-color "var(--bg-color)"}]

    [:.viewport-frame {:margin "1.5rem"
                       :padding "1.5rem"
                       :border "1.5px solid var(--text-color)"
                       :min-height "calc(100svh - 3rem)"
                       :box-sizing "border-box"}]

    [:.font-syncopate {:font-family "'Syncopate', sans-serif"
                       :text-transform "uppercase"
                       :letter-spacing "0.05em"}]

    [:h1 :h2 :h3 :h4 :h5 :h6 {:font-family "'Syncopate', sans-serif"
                               :color "var(--text-color)"
                               :text-transform "uppercase"
                               :letter-spacing "0.05em"}]

    [:main {:max-width "800px"
            :margin "0 auto"}]

    [:h1 {:font-size "2.2rem"
          :margin-bottom "0.5rem"}]

    (at-keyframes :star-pop
              [:0%   {:opacity "0" :transform "translate(-50%, 20px) scale(0.5)"}]
              [:15%  {:opacity "1" :transform "translate(-50%, 0) scale(1)"}]
              [:85%  {:opacity "1" :transform "translate(-50%, 0) scale(1)"}]
              [:100% {:opacity "0" :transform "translate(-50%, -30px) scale(1.2)"}])

    [:.notificacion-overlay
     {:position "fixed"
      :top "15%"
      :left "50%"
      :z-index "9999"
      :animation "star-pop 3s ease-in-out forwards"
      :pointer-events "none"
      :width "80px"
      :height "80px"
      :display "flex"
      :align-items "center"
      :justify-content "center"}]

    [:.azul [:path {:fill "var(--accent-blue)"}]]
    [:.negro [:path {:fill "var(--text-color)"}]]

    [:.svg-viewport {:width "100%"
                     :height "100%"
                     :display "flex"
                     :align-items "center"
                     :justify-content "center"
                     :position "relative"}]

    [:.svg-viewport :svg {:max-width "100%"
                          :max-height "100%"}]

    [:input {:background "transparent"
             :border "1.5px solid var(--text-color)"
             :padding "0.75rem 1rem"
             :font-family "inherit"
             :font-size "inherit"
             :color "var(--text-color)"
             :outline "none"
             :border-radius "0"}]

    [:.demo {
             :position "absolute"
             :z-index 2
             :left "14%"
             :top "35%"
             :width "28vw"
             :font-size "3rem"
             :display "flex"
             :flex-direction "column"}]))

(def login
  (css
    [:.contenedor {:max-width "400px"
                   :margin "4rem auto 0 auto"
                   :display "flex"
                   :flex-direction "column"
                   :align-items "stretch"}]

    [:form {:display "flex"
            :flex-direction "column"
            :gap "1.25rem"
            :margin-top "2rem"}]

    [:.input-group {:display "flex"
                    :flex-direction "column"
                    :gap "0.5rem"}]

    [:button {:background "var(--text-color)"
              :color "var(--bg-color)"
              :border "none"
              :padding "0.85rem"
              :font-family "'Syncopate', sans-serif"
              :font-size "0.9rem"
              :letter-spacing "0.05em"
              :text-transform "uppercase"
              :cursor "pointer"
              :margin-top "1rem"}]))

(def perfil-usuario
  (css {:pretty-print? false}
       [:.contenedor {:display "flex"
                      :flex-direction "column"
                      :justify-content "space-between"
                      :height "calc(100svh - 6rem)"
                      :box-sizing "border-box"}]

       [:.header {:display "flex"
                  :justify-content "space-between"
                  :align-items "center"
                  :width "100%"}]

       [:.avatar {:width "48px"
                  :height "48px"
                  :border "1.5px solid var(--text-color)"
                  :background-color "var(--line-color)"}]

       [:.centrado {:display "flex"
                    :flex-direction "column"
                    :align-items "center"
                    :justify-content "center"
                    :text-align "center"
                    :flex-grow "1"}]

       [:.numero-slide {:font-size "30vw"
                        :line-height "1"}]

       [:.label {:font-family "'Darker Grotesque', sans-serif"
                 :font-size "1.25rem"
                 :text-transform "uppercase"
                 :letter-spacing "0.05em"
                 :margin-top "0.5rem"}]

       [:.footer {:display "flex"
                  :justify-content "flex-end"
                  :width "100%"}]

       [:button.estrella {:background "transparent"
                          :border "1.5px solid var(--text-color)"
                          :width "56px"
                          :height "56px"
                          :display "flex"
                          :align-items "center"
                          :justify-content "center"
                          :color "var(--text-color)"}]

       [:button.atras {:background "var(--text-color)"
                       :color "var(--bg-color)"
                       :border "none"
                       :padding "0.85rem"
                       :font-family "'Syncopate', sans-serif"
                       :font-size "0.9rem"
                       :letter-spacing "0.05em"
                       :text-transform "uppercase"
                       :cursor "pointer"
                       :margin-top "1rem"}]

       [:svg {:padding "0.25rem"}]))

(def podio
  (css {:pretty-print? false}
    [:.contenedor {:display "flex"
                   :flex-direction "column"
                   :justify-content "space-between"
                   :height "calc(100svh - 6rem)"
                   :box-sizing "border-box"}]

    [:.podio-grid {:display "flex"
                   :justify-content "center"
                   :align-items "flex-end"
                   :gap "1.5rem"
                   :margin "2rem 0"
                   :flex-grow "1"}]

    [:.podio-col {:display "flex"
                  :flex-direction "column"
                  :align-items "center"
                  :justify-content "flex-start"
                  :border "1.5px solid var(--text-color)"
                  :padding "1.5rem 1rem"
                  :flex "1"
                  :max-width "220px"
                  :box-sizing "border-box"}]

    [:.posicion-1 {:height "85%"
                   :border-color "var(--accent-blue)"}
     [:.posicion {:color "var(--accent-blue)"}]]

    [:.posicion-2 {:height "70%"}]

    [:.posicion-3 {:height "55%"}]

    [:.avatar {:width "48px"
               :height "48px"
               :border "1.5px solid var(--text-color)"
               :background-color "var(--line-color)"
               :margin "1rem 0"}]

    [:.posicion {:font-size "2rem"}]

    [:.nombre {:font-size "1rem"
               :text-align "center"}]

    [:.estrellas {:display "flex"}
     [:svg {:width "1.5rem" :fill "var(--accent-blue)"}]
     [:span {:font-size "2rem"}]]))
