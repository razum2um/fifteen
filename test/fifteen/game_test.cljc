(ns fifteen.game-test
  (:require [fifteen.game :as game]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            #?(:clj [clojure.test :as t]
               :cljs [cljs.test :as t :include-macros true])))

(t/deftest game-move-test
  (let [given [[::game/nil 1] [2 3]]
        expected [[2 1] [::game/nil 3]]]
    (t/is (= expected (game/game-move given [1 0])))))

(def n (gen/elements (range 4)))
(def test-game (gen/shuffle (game/seed-game)))
(def game-with-moving-idxs (gen/tuple (gen/fmap game/build-game test-game)
                                      (gen/tuple n n)))

(def prop-any-move-dont-change-ability-to-solve
  (prop/for-all [[game moving-idxs] game-with-moving-idxs]
                (= (game/solvable? game)
                   (game/solvable? (game/game-move game moving-idxs)))))

(t/deftest game-constant-solvable-test
  (t/is (->> prop-any-move-dont-change-ability-to-solve
             (tc/quick-check 100)
             :result)))
