(ns flocking.behaviors.simulation
  (:require [flocking.behaviors.rules :as rules]
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

(defn sim-boid [{:keys [state behaviors max-speed] :as boid } world-state dt]
  (let [[pos vel] state
        forces (vals (rules/compute-steering-forces behaviors boid world-state))
        vprime (vec/add vel (map #(* % dt) (apply map + forces)))
        vmag (vec/mag vprime)
        vp (if (zero? vmag) vel (map #(* max-speed (/ % vmag)) vprime))
        new-states [(vec/add pos (vec/scale vp dt)) vp]
        new-behaviors (rules/update-behaviors behaviors boid world-state)]
    (-> boid
        (assoc :state new-states)
        (assoc :behaviors new-behaviors)
        (wrap (:world world-state)))))

(defn averages [{:keys [flock] :as state}]
  (->> flock
       (map :state)
       (apply map vector)
       (map #(apply map + %))
       (map #(vec/scale % (/ 1.0 (count flock))))
       (zipmap [:average-position :average-velocity])
       (into state)))

(defn sim[state]
  (let [t (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
        dt (* (- t (state :time t)) 1E-3)
        ;One line to go from one to many
        new-flock (for [boid (:flock state)] (sim-boid boid state dt))]
    ;(averages (into state { :time t :flock new-flock }))
    (-> state
        (into { :time t :flock new-flock })
        averages)))