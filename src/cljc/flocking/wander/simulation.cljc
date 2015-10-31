(ns flocking.wander.simulation
  (:require [flocking.wander.rules :as rules]
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

;Multimethod for steering
(defmulti steer (fn[behavior-name _ _ _] behavior-name))
(defmethod steer :wander [_ behavior boid flock] (rules/wander behavior boid flock))

(defn sim-boid [{:keys [state behaviors max-speed] :as boid } world-state dt]
  (let [[pos vel] state
        ;NOTE: Added basic integrator for behaviors
        forces (for [[b behavior] behaviors] (steer b behavior boid world-state))
        vprime (vec/add vel (map #(* % dt) (apply map + forces)))
        vmag (vec/mag vprime)
        vp (if (zero? vmag) vel (map #(* max-speed (/ % vmag)) vprime))
        new-states [(vec/add pos (vec/scale vp dt)) vp]]
    (-> boid
        (assoc-in [:state] new-states)
        ;TODO - fix bad mojo here!!!!
        (update-in [:behaviors :wander] rules/update-wander)
        (wrap (:world world-state)))))

(defn sim[state]
  (let [t (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
        dt (* (- t (state :time t)) 1E-3)]
    (into state { :time t :boid (sim-boid (:boid state) state dt) }) ))