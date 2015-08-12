(ns flocking.game-launcher
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]
    [quil.middleware :as m]
    [flocking.simulation :as sim]
    [flocking.quil-renderer :as qr]))

(defn setup []
  (q/smooth)
  (q/frame-rate 100)
  {:world { :minx -10 :maxx 10 :miny -10 :maxy 10 }
   :boid {:width 0.6 :height 1.0 :state [0 0 0 0 0]
          :behaviors { :wander { :wander-direction 0.0 :wander-rate 0.6 :wander-strength 1.0 } }}})


;(defn setup []
;  (q/smooth)
;  (q/frame-rate 100)
;  {:world { :minx -10 :maxx 10 :miny -10 :maxy 10 }
;   :entities { 1 {:width 0.6 :height 1.0 :state [0 0 0 0 0]
;                  :behaviors { :wander { :wander-direction 0.0 :wander-rate 0.6 :wander-strength 1.0 } }}
;              2 {:width 0.6 :height 1.0 :state [0 50 50 0 0]
;                 :behaviors { :wander { :wander-direction 0.0 :wander-rate 0.6 :wander-strength 1.0 } }}}})

(defn launch-sketch [{:keys[width height host]}]
  (q/sketch
    :title "Flocking Behaviors"
    #?@(:cljs [:host host])
    :setup setup
    :draw qr/draw
    :update sim/sim
    :middleware [m/fun-mode]
    :size [width height]))

;#?(:clj (launch-sketch { :width 800 :height 600 }))

#?(:cljs (defn ^:export launch-app[host width height]
           (launch-sketch { :width width :height height :host host})))