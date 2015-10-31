(ns flocking.behaviors.rules
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

;New method for combining forces
(defn weighted-vec [vec strength average-vec]
  (let [dv (vec/sub average-vec vec)
        mag (vec/mag dv)]
    (if (zero? mag)
      [0 0]
      (vec/scale dv (/ strength mag)))))

;Three more forces
(defn separate [{:keys [range strength]} {:keys [state]} {:keys [flock]}]
  (let [nearby (for [{[p] :state } flock
                     :let [dp (vec/sub (state 0) p) m (vec/mag dp)]
                     :when (<= 0 m range) ]
                 (if (zero? m) (state 0) (vec/scale dp (/ strength m m))))]
    (apply mapv + nearby)))

(defn align [{:keys [strength] } {:keys [state]} {:keys [average-velocity] }]
  (weighted-vec (state 1) strength average-velocity))

(defn cohere [{:keys [strength] } {:keys [state]} {:keys [average-position] }]
  (weighted-vec (state 0) strength average-position))

;Multimethod for steering
(defmulti steer (fn[behavior-name _ _ _] behavior-name))
(defmethod steer :wander [_ behavior boid flock] (wander behavior boid flock))
(defmethod steer :separate [_ behavior boid flock] (separate behavior boid flock))
(defmethod steer :align [_ behavior boid flock] (align behavior boid flock))
(defmethod steer :cohere [_ behavior boid flock] (cohere behavior boid flock))
(defmethod steer :default [_ _ _ _] [0 0])

;(defn steer [behavior-key behavior boid flock]
;  (case behavior-key
;    :wander (wander behavior boid flock)
;    :separate (separate behavior boid flock)
;    :align (align behavior boid flock)
;    :cohere (cohere behavior boid flock)
;    [0 0]))

;Multimethod for updates to behaviors
(defmulti update-behavior (fn[behavior-name _ _ _] behavior-name))
(defmethod update-behavior :wander [_ behavior _ _] (update-wander behavior))
(defmethod update-behavior :default [_ behavior _ _] behavior)

;(defn update-behavior [behavior-key behavior boid flock]
;  (case behavior-key
;    :wander (update-wander behavior)
;    behavior))

;Methods to iterate over updates
(defn behaviors-calc[f behaviors boid world-state]
  (into {} (for [[b behavior] behaviors] [b (f b behavior boid world-state)])))

(def compute-steering-forces (partial behaviors-calc steer))
(def update-behaviors (partial behaviors-calc update-behavior))

;Create a random wander behavior
(defn gen-wander []
  { :direction (* 2 Math/PI (Math/random))
   :rate (Math/random)
   :strength (* 10 (Math/random)) })