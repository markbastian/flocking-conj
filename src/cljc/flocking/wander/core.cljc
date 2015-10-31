(ns flocking.wander.core
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]
    [quil.middleware :as m]
    [flocking.wander.simulation :as sim]
    [flocking.wander.renderer :as qr]))

(def initial-state
  {:world { :minx (- 20) :maxx 20 :miny (- 20) :maxy 20 }
   :boid {:width 0.6
          :height 1.0
          :color [0 255 0]
          :max-speed 5.0
          :state [[0 0] [0 5]]
          ;Adding a behavior!
          :behaviors
          { :wander { :direction 0 ;Range [0, 2*pi)
                     ;Make nonzero so it actually changes.
                     :rate 0.5 ; [0, 1)
                     :strength 10 ;Can be anything
                     }}}})

(defn launch-sketch [{:keys[width height host]}]
  (q/sketch
    :title "Flocking Behaviors"
    #?@(:cljs [:host host])
    :setup (fn [] initial-state)
    :draw qr/draw
    :update sim/sim
    :middleware [m/fun-mode]
    :size [width height]))

;#?(:clj (launch-sketch { :width 600 :height 600 :num-boids 100 }))

#?(:cljs (defn ^:export launch-app[host width height]
           (launch-sketch { :width width :height height :host host })))