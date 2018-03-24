(ns fifteen.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def app-state (atom {:game (mapv vec (partition 4 (shuffle (cons ::nil (range 1 16)))))}))
(defn l [x] (println x) x)

(defn update-game [game pos2 val1 pos1]
  (-> game
      (assoc-in pos2 val1)
      (assoc-in pos1 ::nil)))

(def self [identity identity])
(def to-left [identity dec])
(def to-right [identity inc])
(def to-up [dec identity])
(def to-down [inc identity])
(defn ->pos-idxs [ff nn]
  (mapv (fn [f n] (f n)) ff nn))

(defn game-move [game moving-idxs]
  (let [self (get-in game moving-idxs)
        ->idxs #(->pos-idxs % moving-idxs)
        update-to #(update-game game (->idxs %) self moving-idxs)
        can? #(= ::nil (get-in game (->idxs %)))]
    (cond
      (can? to-left) (update-to to-left)
      (can? to-right) (update-to to-right)
      (can? to-up) (update-to to-up)
      (can? to-down) (update-to to-down))))

(defn redraw [moving-idxs]
  (swap! app-state
         update-in
         [:game]
         (fn [game] (game-move game moving-idxs))))

(defn squares [game]
  [:div
   (for [[group-idx group] (map-indexed vector game)]
     [:div.group
      (for [[idx x] (map-indexed vector group)]
        [:div.square {:on-click #(redraw [group-idx idx])} x])])])

(defn app []
  [:div [squares (:game @app-state)]])

(reagent/render-component [app] (. js/document (getElementById "app")))

(defn on-js-reload [])
