(ns fifteen.core
  (:require [reagent.core :as reagent :refer [atom]]
            [fifteen.game :as game]))

(enable-console-print!)

(def app-state (atom {:game (game/gen-game)}))

;; actions

(defn redraw [moving-idxs]
  (swap! app-state
         update-in
         [:game]
         (fn [game] (game/game-move game moving-idxs))))

(defn show-solvable []
  (loop [game (game/gen-game)]
    (if (solvable? game)
      (swap! app-state assoc-in [:game] game)
      (recur (game/gen-game)))))

;; views

(defn squares [game]
  [:div
   [:h1 (if (solvable? game) "Solvable" "Not solvable")]
   (if (solved? game) [:h2 "Solved!"])
   (for [[group-idx group] (map-indexed vector game)]
     ^{:key group-idx} [:div.group
      (for [[idx x] (map-indexed vector group)]
        ^{:key idx} [:div.square {:on-click #(redraw [group-idx idx])} (when-not (= ::game/nil x) x)])])])

(defn app []
  [:div
   [squares (:game @app-state)]
   [:button {:on-click show-solvable} "Show solvable"]])

(reagent/render-component [app] (. js/document (getElementById "app")))

(defn on-js-reload [])
