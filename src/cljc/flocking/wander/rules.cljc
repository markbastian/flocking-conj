(ns flocking.wander.rules
  (:require [vecmath.vec :as vec]))

(defn update-wander [{:keys [rate] :as s}]
  (update s :direction #(+ % (* (- (rand) 0.5) rate))))

(defn tovec[m d]((juxt #(* m (Math/cos %)) #(* m (Math/sin %))) d))

(defn wander [{:keys [direction strength]} {:keys [state]} _]
  (let [velocity (state 1)
        m (vec/mag velocity)
        f (if (zero? m) velocity (map #(* (Math/sqrt 2.0) (/ % m)) velocity))
        w (tovec strength direction)]
    (vec/add f w)))