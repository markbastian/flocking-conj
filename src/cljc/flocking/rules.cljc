(ns flocking.rules
  (:require [vecmath.vec :as vec]))

;http://www.red3d.com/cwr/steer/Wander.html
;https://www.khanacademy.org/computer-programming/boid-seek-arrive-wander-behaviors/5939360759808000

(defn update-wander [{:keys [rate] :as s}]
  (update s :direction #(+ % (* (- (rand) 0.5) rate))))

(defn tovec[m d]((juxt #(* m (Math/cos %)) #(* m (Math/sin %))) d))

(defn wander [{:keys [state]} {:keys [direction strength]}]
  (let [velocity (state 1)
        m (vec/mag velocity)
        f (if (zero? m) velocity (map #(* (Math/sqrt 2.0) (/ % m)) velocity))
        w (tovec strength direction)]
    (vec/add f w)))

(defn separate [{:keys [state flock]}  {:keys [range strength]}]
  (let [nearby (for [{[p] :state } flock
                     :let [dp (vec/sub (state 0) p) m (vec/mag dp)]
                     :when (<= 0 m range) ]
                 (if (zero? m) (state 0) (vec/scale dp (/ strength m m))))]
    (apply mapv + nearby)))

(defn weighted-vec [vec strength average-vec]
  (let [dv (vec/sub average-vec vec)
        mag (vec/mag dv)]
    (if (zero? mag)
      [0 0]
      (vec/scale dv (/ strength mag)))))

(defn align [{:keys [state average-velocity]} {:keys [strength] }]
  (weighted-vec (state 1) strength average-velocity))

(defn cohere [{:keys [state average-position]} {:keys [strength] }]
  (weighted-vec (state 0) strength average-position))

(defn gen-wander []
  { :direction (* 2 Math/PI (Math/random))
   :rate (Math/random)
   :strength (* 10 (Math/random)) })