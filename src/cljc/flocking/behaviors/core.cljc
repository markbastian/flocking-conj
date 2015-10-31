(ns flocking.behaviors.core
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]
    [quil.middleware :as m]
    [flocking.behaviors.simulation :as sim]
    [flocking.behaviors.renderer :as qr]
    [flocking.behaviors.rules :as rules]))

;Conveniece methods for generating a flock
(defn rand-range [lo hi]
  (+ lo (* (- hi lo) (Math/random))))

(defn gen-state [{:keys [minx maxx miny maxy]}]
  [[(rand-range minx maxx) (rand-range miny maxy)] [0 0]])

(defn initial-state [flock-size]
  (let [dim 20
        world { :minx (- dim) :maxx dim :miny (- dim) :maxy dim }]
    {:world world
     :flock (for [_ (range flock-size)]
              {:width 0.6
               :height 1.0
               :color [0 255 0]
               :max-speed 5.0
               :state (gen-state world)
               :behaviors
               { :wander (assoc (rules/gen-wander) :strength 10)
                :separate { :range 2 :strength 10 }
                :align { :strength 10 }
                :cohere { :strength 50 }}})}))

(defn launch-sketch [{:keys[width height host flock-size]}]
  (q/sketch
    :title "Flocking Behaviors"
    #?@(:cljs [:host host])
    :setup #(initial-state flock-size)
    :draw qr/draw
    :update sim/sim
    :middleware [m/fun-mode]
    :size [width height]))

;Uncomment to run Clojure version in REPL
;#?(:clj (launch-sketch { :width 600 :height 600 :flock-size 100 }))

#?(:cljs (defn ^:export launch-app[host width height flock-size]
           (launch-sketch { :width width :height height :host host :flock-size flock-size})))