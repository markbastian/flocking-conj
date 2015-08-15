(ns flocking.game-launcher
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]
    [quil.middleware :as m]
    [flocking.simulation :as sim]
    [flocking.rules :as rules]
    [flocking.quil-renderer :as qr]))

(def dim 20)
(defn setup [num-boids]
  (q/smooth)
  (q/frame-rate 30)
  {:world { :minx (- dim) :maxx dim :miny (- dim) :maxy dim }
   :boids (for [_ (range num-boids)]
            {:width 0.6 :height 1.0 :state [[(- (* 2 dim (Math/random)) dim) (- (* 2 dim (Math/random)) dim)] [0 0]] :behaviors { :wander (rules/gen-wander) }})})

(defn launch-sketch [{:keys[width height host num-boids]}]
  (q/sketch
    :title "Flocking Behaviors"
    #?@(:cljs [:host host])
    :setup #(setup num-boids)
    :draw qr/draw
    :update sim/sim
    :middleware [m/fun-mode]
    :size [width height]))

;#?(:clj (launch-sketch { :width 600 :height 600 :num-boids 100 }))

#?(:cljs (defn ^:export launch-app[host width height num-boids]
           (launch-sketch { :width width :height height :host host :num-boids num-boids })))