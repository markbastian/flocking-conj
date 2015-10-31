(ns flocking.data.core
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]
    [quil.middleware :as m]))

(def initial-state
  {:world { :minx (- 20) :maxx 20 :miny (- 20) :maxy 20 }
   :boid {:width 0.6
          :height 1.0
          :color [0 255 0]
          :state [[0 0] [0 0]]}})

(defn draw-boid [{[pos [vx vy]] :state :keys [width height color]}]
  (let [tw (* 0.5 width) th (* 0.5 height)]
    (q/with-translation
      pos
      (q/with-rotation
        [(Math/atan2 vy vx)]
        (q/with-rotation
          [(q/radians -90)]
          (q/no-fill)
          (apply q/stroke color)
          (q/triangle (- tw) (- th) 0 th tw (- th)))))))

(defn draw [{:keys [world boid]}]
  (let [{ :keys [minx maxx miny maxy] } world
        dx (- maxx minx) dy (- maxy miny)
        max-world-dim (max dx dy)
        w (q/width) h (q/height)
        min-screen-dim (min w h)]
    (do
      (q/background 0 0 0)
      (q/translate (* 0.5 w) (* 0.5 h))
      (q/scale 1 -1)
      (q/scale (/ min-screen-dim max-world-dim))
      (q/stroke-weight (/ max-world-dim min-screen-dim 0.5))
      (draw-boid boid))))

(defn launch-sketch [{:keys[width height host]}]
  (q/sketch
    :title "Flocking Behaviors"
    #?@(:cljs [:host host])
    :setup (fn [] initial-state)
    :draw draw
    :middleware [m/fun-mode]
    :size [width height]))

;#?(:clj (launch-sketch { :width 600 :height 600 :num-boids 100 }))

#?(:cljs (defn ^:export launch-app[host width height]
           (launch-sketch { :width width :height height :host host })))