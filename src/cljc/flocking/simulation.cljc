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

(defn wrap [{{:keys [minx maxx miny maxy]} :world :as s}]
  (-> s
      (update-in [:boid :state 1] #(w % minx maxx))
      (update-in [:boid :state 2] #(w % miny maxy))))

(defn sim[{:keys [time boid] :as s}]
  (let [{:keys [state behaviors] } boid
        t (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
        dt (* (- t (or time t)) 1E-3)
        pos (subvec state 1 3)
        vel (subvec state 3 5)
        wander-acceleration (rules/wander vel (behaviors :wander))
        tprime (+ t dt)
        dv (map #(* % dt) wander-acceleration)
        vprime (map + vel dv)
        vmag (Math/sqrt (reduce + (map * vprime vprime)))
        vp (if (zero? vmag) vel (map #(* 1.0 (/ % vmag)) vprime))
        dp (map #(* % dt) vp)
        pprime (map + pos dp)
        new-states (reduce into [tprime] [pprime vp])]
    (-> s
        (into { :time t })
        (assoc-in [:boid :state] new-states)
        (update-in [:boid :behaviors :wander] rules/update-wander)
        wrap)))
