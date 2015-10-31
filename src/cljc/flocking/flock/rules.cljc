(ns flocking.flock.rules
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

;Multimethod for steering
(defmulti steer (fn[behavior-name _ _ _] behavior-name))
(defmethod steer :wander [_ behavior boid flock] (wander behavior boid flock))
(defmethod steer :default [_ _ _ _] [0 0])

;Multimethod for updates to behaviors
(defmulti update-behavior (fn[behavior-name _ _ _] behavior-name))
(defmethod update-behavior :wander [_ behavior _ _] (update-wander behavior))
(defmethod update-behavior :default [_ behavior _ _] behavior)

;Methods to iterate over updates
(defn behaviors-calc[f behaviors boid world-state]
  (into {} (for [[b behavior] behaviors] [b (f b behavior boid world-state)])))

(def compute-steering-forces (partial behaviors-calc steer))
(def update-behaviors (partial behaviors-calc update-behavior))