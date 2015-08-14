(ns flocking.simulation
  (:require [flocking.rules :as rules]
            [vecmath.vec :as vec]))

(defn w [v lo hi]
  (let [delta (- hi lo)]
    (loop [x v]
      (cond
        (< x lo)
        (recur (+ x delta))
        (>= x hi)
        (recur (- x delta))
        :else x))))

(defn wrap [state {:keys [minx maxx miny maxy]}]
  (-> state
      (update-in [:state 0 0] #(w % minx maxx))
      (update-in [:state 0 1] #(w % miny maxy))))

(defn sim-boid [{:keys [state behaviors] :as boid-state } boids dt world ap av]
  (let [[pos vel] state
        nearby (for [{[p] :state } boids
                     :let [dp (vec/sub pos p) m (vec/mag dp)]
                     :when (<= 0 m 4.0) ]
                 (if (zero? m) pos (vec/scale dp (/ 5 m m)))) ;separation factor
        separation-acceleration (apply mapv + nearby)
        alignment-acceleration av
        cohesion-acceleration (vec/scale (vec/sub ap pos) 8) ;Cohesion factor
        wander-acceleration (rules/wander vel (behaviors :wander))
        forces (map +
                    separation-acceleration
                    alignment-acceleration
                    cohesion-acceleration
                    wander-acceleration)
        vprime (vec/add vel (map #(* % dt) forces))
        vmag (vec/mag vprime)
        ;5.0 below is the max vel. Consider parameterizing
        vp (if (zero? vmag) vel (map #(* 5.0 (/ % vmag)) vprime))
        new-states [(vec/add pos (vec/scale vp dt)) vp]]
    (-> boid-state
        (assoc-in [:state] new-states)
        (update-in [:behaviors :wander] rules/update-wander)
        (wrap world))))

(defn averages [{:keys [boids]}]
  (->> boids
       (map :state)
       (apply map vector)
       (map #(apply mapv + %))
       (mapv #(vec/scale % (/ 1.0 (count boids))))))

(defn sim[{:keys [time boids world] :as s}]
  (let [t (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
        dt (* (- t (or time t)) 1E-3)
        [ap av] (averages s)
        new-boids (for [boid boids] (sim-boid boid boids dt world ap av))]
    (-> s
        (into { :time t })
        (into { :boids new-boids }))))
