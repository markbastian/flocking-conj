(ns flocking.behaviors.core
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]
    [quil.middleware :as m]
    [flocking.behaviors.simulation :as sim]
    [flocking.behaviors.renderer :as qr]))

(def initial-state
  {:world { :minx (- 20) :maxx 20 :miny (- 20) :maxy 20 }
   :flock [{:width 0.6 :height 1.0 :color [255 0 0] :max-speed 1.0 :state [[0 0] [0 5]]
            :behaviors { :wander { :direction 0 :rate 01.0 :strength 10 :debug true }}}
           {:width 0.6 :height 1.0 :color [0 255 0] :max-speed 2.0 :state [[0 10] [-5 5]]
            :behaviors { :wander { :direction 1 :rate 0.8 :strength 10 :debug true }}}
           {:width 0.6 :height 1.0 :color [0 0 255] :max-speed 4.0 :state [[0 -10] [5 5]]
            :behaviors { :wander { :direction 2 :rate 0.6 :strength 10 :debug true }}}
           {:width 0.6 :height 1.0 :color [255 255 0] :max-speed 6.0 :state [[10 0] [-5 -5]]
            :behaviors { :wander { :direction 3 :rate 0.4 :strength 10 :debug true }}}
           {:width 0.6 :height 1.0 :color [0 255 255] :max-speed 8.0 :state [[-10 0] [5 0]]
            :behaviors { :wander { :direction 4 :rate 0.2 :strength 10 :debug true }}}
           {:width 0.6 :height 1.0 :color [255 0 255] :max-speed 10.0 :state [[-10 10] [-5 5]]
            :behaviors { :wander { :direction 5 :rate 0.1 :strength 10 :debug true }}}]})

(defn launch-sketch [{:keys[width height host]}]
  (q/sketch
    :title "Flocking Behaviors"
    #?@(:cljs [:host host])
    :setup (fn [] initial-state)
    :draw qr/draw
    :update sim/sim
    :middleware [m/fun-mode]
    :size [width height]))

;Uncomment to run Clojure version in REPL
;#?(:clj (launch-sketch { :width 600 :height 600 :num-flock 100 }))

#?(:cljs (defn ^:export launch-app[host width height]
           (launch-sketch { :width width :height height :host host })))