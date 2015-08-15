(ns flocking.rules
  (:require [vecmath.vec :as vec]))

;http://www.red3d.com/cwr/steer/Wander.html
;https://www.khanacademy.org/computer-programming/boid-seek-arrive-wander-behaviors/5939360759808000

(defn update-wander [{:keys [wander-rate] :as s}]
  (update s :wander-direction #(+ % (* (- (rand) 0.5) wander-rate))))

(defn tovec[m d]((juxt #(* m (Math/cos %)) #(* m (Math/sin %))) d))

(defn wander [velocity {:keys [wander-direction wander-strength]}]
  (let [m (vec/mag velocity)
        f (if (zero? m) velocity (map #(* (Math/sqrt 2.0) (/ % m)) velocity))
        w (tovec wander-strength wander-direction)]
    (vec/add f w)))

(defn gen-wander []
  { :wander-direction (* 2 Math/PI (Math/random))
   :wander-rate (Math/random)
   :wander-strength (* 5 (Math/random)) })