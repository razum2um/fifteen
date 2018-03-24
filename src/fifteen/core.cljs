(ns fifteen.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defn gen-game []
  (mapv vec (partition 4 (shuffle (cons ::nil (range 1 16))))))

(def app-state (atom {:game (gen-game)}))
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
      (can? to-down) (update-to to-down)
      :else game)))

;; prediction

(defn forward-less [n coll] (count (filter #(< % n) coll)))

(defn ->snake [game]
  (remove #{::nil}
          (concat (game 0)
                  (reverse (game 1))
                  (game 2)
                  (reverse (game 3)))))

(defn solvable? [game-as-snake]
  (odd? (loop [[x & more] game-as-snake acc 0]
          (if x (recur more (+ acc (forward-less x more)))
              acc))))

(defn solved? [game]
  (let [game* (remove #{::nil} (flatten game))]
    (= (sort game*) game*)))

;; actions

(defn redraw [moving-idxs]
  (swap! app-state
         update-in
         [:game]
         (fn [game] (game-move game moving-idxs))))

(defn show-solvable []
  (loop [game (gen-game)]
    (if (-> game ->snake solvable?)
      (swap! app-state assoc-in [:game] game)
      (recur (gen-game)))))

;; views

(defn squares [game]
  [:div
   [:h1 (if (-> game ->snake solvable?) "Solvable" "Not solvable")]
   (if (-> game solved?)
     [:h2 "Solved!"])
   (for [[group-idx group] (map-indexed vector game)]
     ^{:key group-idx} [:div.group
      (for [[idx x] (map-indexed vector group)]
        ^{:key idx} [:div.square {:on-click #(redraw [group-idx idx])} (when-not (= ::nil x) x)])])])

(defn app []
  [:div
   [squares (:game @app-state)]
   [:button {:on-click show-solvable} "Show solvable"]])

(reagent/render-component [app] (. js/document (getElementById "app")))

(defn on-js-reload [])
