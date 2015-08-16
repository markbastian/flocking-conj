(ns flocking.rules
  (:require [vecmath.vec :as vec]))

;http://www.red3d.com/cwr/steer/Wander.html
;https://www.khanacademy.org/computer-programming/boid-seek-arrive-wander-behaviors/5939360759808000

(defn update-wander [{:keys [rate] :as s}]
  (update s :direction #(+ % (* (- (rand) 0.5) rate))))

(defn tovec[m d]((juxt #(* m (Math/cos %)) #(* m (Math/sin %))) d))

(defn wander [[_ velocity] {:keys [direction strength]}]
  (let [m (vec/mag velocity)
        f (if (zero? m) velocity (map #(* (Math/sqrt 2.0) (/ % m)) velocity))
        w (tovec strength direction)]
    (vec/add f w)))

(defn separate [[pos _] separation boids]
  (let [{:keys [range strength]} separation
        nearby (for [{[p] :state } boids
                     :let [dp (vec/sub pos p) m (vec/mag dp)]
                     :when (<= 0 m range) ]
                 (if (zero? m) pos (vec/scale dp (/ strength m m))))]
    (apply mapv + nearby)))

(defn weighted-vec [vec strength average-vec]
  (let [dv (vec/sub average-vec vec)
        mag (vec/mag dv)]
    (if (zero? mag)
      [0 0]
      (vec/scale dv (/ strength mag)))))

(defn align [[_ vel] {:keys [strength] } average-vel]
  (weighted-vec vel strength average-vel))

(defn cohere [[pos _] {:keys [strength] } average-pos]
  (weighted-vec pos strength average-pos))

;(defn align [[_ vel] {:keys [strength] } average-vel]
;  (vec/scale (vec/sub average-vel vel) strength))

;(defn cohere [[pos _] {:keys [strength] } average-pos]
;  (vec/scale (vec/sub average-pos pos) strength))

(defn gen-wander []
  { :direction (* 2 Math/PI (Math/random))
   :rate (Math/random)
   :strength (* 10 (Math/random)) })