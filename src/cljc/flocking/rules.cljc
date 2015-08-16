(ns flocking.rules
  (:require [vecmath.vec :as vec]))

;http://www.red3d.com/cwr/steer/Wander.html
;https://www.khanacademy.org/computer-programming/boid-seek-arrive-wander-behaviors/5939360759808000

(defn update-wander [{:keys [wander-rate] :as s}]
  (update s :wander-direction #(+ % (* (- (rand) 0.5) wander-rate))))

(defn tovec[m d]((juxt #(* m (Math/cos %)) #(* m (Math/sin %))) d))

(defn wander [[_ velocity] {:keys [wander-direction wander-strength]}]
  (let [m (vec/mag velocity)
        f (if (zero? m) velocity (map #(* (Math/sqrt 2.0) (/ % m)) velocity))
        w (tovec wander-strength wander-direction)]
    (vec/add f w)))

(defn separate [[pos _] separation boids]
  (let [{:keys [range strength]} separation
        nearby (for [{[p] :state } boids
                     :let [dp (vec/sub pos p) m (vec/mag dp)]
                     :when (<= 0 m range) ]
                 (if (zero? m) pos (vec/scale dp (/ strength m m))))]
    (apply mapv + nearby)))

(defn align [[_ vel] {:keys [strength] } average-vel]
  (vec/scale (vec/sub average-vel vel) strength))

(defn cohere [[pos _] {:keys [strength] } average-pos]
  (vec/scale (vec/sub average-pos pos) strength))

(defn gen-wander []
  { :wander-direction (* 2 Math/PI (Math/random))
   :wander-rate (Math/random)
   :wander-strength (* 1 (Math/random)) })