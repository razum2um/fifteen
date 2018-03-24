(ns fifteen.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(def app-state (atom {:game (mapv vec (partition 4 (shuffle (cons ::nil (range 1 16)))))}))
(defn l [x] (println x) x)

(defn update-game [state pos1 val1 pos2 val2]
  (update-in state [:game]
             (fn [game] (-> game
                            (assoc-in pos1 val1)
                            (assoc-in pos2 val2)
                            l))))

(defn move [group-idx idx]
  (let [game (:game @app-state)
        self-pos [group-idx idx]
        left-pos [group-idx (dec idx)]
        right-pos [group-idx (inc idx)]
        up-pos [(dec group-idx) idx]
        down-pos [(inc group-idx) idx]

        self (get-in game self-pos)
        left (get-in game left-pos)
        right (get-in game right-pos)
        up (get-in game up-pos)
        down (get-in game down-pos)

        update-to (fn [state pos1 val2] (update-game state pos1 self self-pos val2))]
    (cond
      (= ::nil left) (swap! app-state update-to left-pos left)
      (= ::nil right) (swap! app-state update-to right-pos right)
      (= ::nil up) (swap! app-state update-to up-pos up)
      (= ::nil down) (swap! app-state update-to down-pos down)
    )))

(defn hello-world []
  [:div
   (for [[group-idx group] (map-indexed vector (:game @app-state))]
     [:div.group
      (for [[idx x] (map-indexed vector group)]
        [:div.square {:on-click #(move group-idx idx)} x])])])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  (swap! app-state update-in [:__figwheel_counter] inc)
  )
