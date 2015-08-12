(ns flocking.simulation
  (:require [flocking.rules :as rules]))

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
      (update-in [:state 0] #(w % minx maxx))
      (update-in [:state 1] #(w % miny maxy))))

(defn sim-boid [{:keys [state behaviors] :as boid-state } dt world]
  (let [pos (subvec state 0 2)
        vel (subvec state 2 4)
        wander-acceleration (rules/wander vel (behaviors :wander))
        dv (map #(* % dt) wander-acceleration)
        vprime (map + vel dv)
        vmag (Math/sqrt (reduce + (map * vprime vprime)))
        vp (if (zero? vmag) vel (map #(* 1.0 (/ % vmag)) vprime))
        dp (map #(* % dt) vp)
        pprime (map + pos dp)
        new-states (reduce into [] [pprime vp])]
    (-> boid-state
        (assoc-in [:state] new-states)
        (update-in [:behaviors :wander] rules/update-wander)
        (wrap world))))

(defn sim[{:keys [time boids world] :as s}]
  (let [t (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
        dt (* (- t (or time t)) 1E-3)
        new-boids (for [boid boids] (sim-boid boid dt world))]
    (-> s
        (into { :time t })
        (into { :boids new-boids }))))
