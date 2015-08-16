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

(defn wall-force [pos wall]
  (let [d (vec/sub pos wall)
        m (vec/mag d)]
    (vec/scale d (/ 1.0 m m m))))

(defn wall-avoidance [[x y :as pos] {:keys [minx maxx miny maxy]}]
  (mapv + (wall-force pos [minx y])
        (wall-force pos [maxx y])
        (wall-force pos [x miny])
        (wall-force pos [x maxy])))

(defn sim-boid [{:keys [state max-speed behaviors] :as boid-state } boids dt world ap av]
  (let [[pos vel] state
        separation-acceleration (rules/separate state (:separation behaviors) boids)
        alignment-acceleration (rules/align state (:alignment behaviors) av)
        cohesion-acceleration (rules/cohere state (:cohesion behaviors) ap)
        wander-acceleration (rules/wander state (:wander behaviors))
        forces (map +
                    separation-acceleration
                    alignment-acceleration
                    cohesion-acceleration
                    wander-acceleration)
        vprime (vec/add vel (map #(* % dt) forces))
        vmag (vec/mag vprime)
        vp (if (zero? vmag) vel (map #(* max-speed (/ % vmag)) vprime))
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
        new-boids (for [boid boids]
                    (sim-boid boid boids dt world ap av))]
    (-> s
        (into { :time t })
        (into { :boids new-boids }))))
