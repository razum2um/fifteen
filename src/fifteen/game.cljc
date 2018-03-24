(ns fifteen.game)

(defn seed-game [] (shuffle (cons ::nil (range 1 16))))

(defn build-game [seed]
  (mapv vec (partition 4 seed)))

(defn gen-game []
  (build-game (seed-game)))

(defn- update-game [game pos2 val1 pos1]
  (-> game
      (assoc-in pos2 val1)
      (assoc-in pos1 ::nil)))

(def self [identity identity])
(def to-left [identity dec])
(def to-right [identity inc])
(def to-up [dec identity])
(def to-down [inc identity])
(defn- ->pos-idxs [ff nn]
  (mapv (fn [f n] (f n)) ff nn))

(defn l [x] (println x) x)
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

(defn- forward-less [n coll] (count (filter #(< % n) coll)))

(defn- ->snake [game]
  (remove #{::nil}
          (concat (game 0)
                  (reverse (game 1))
                  (game 2)
                  (reverse (game 3)))))

(defn solvable? [game]
  (odd? (loop [[x & more] (->snake game) acc 0]
          (if x (recur more (+ acc (forward-less x more)))
              acc))))

(defn solved? [game]
  (let [game* (flatten game)
        nil-last? (= ::nil (last game*))
        game* (remove #{::nil} game*)]
    (and nil-last? (= (sort game*) game*))))
