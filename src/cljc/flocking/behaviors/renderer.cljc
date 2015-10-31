(ns flocking.behaviors.renderer
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]))

(defn debug-wander [{{ {:keys [direction strength rate debug]} :wander } :behaviors }]
  (when debug
    (q/fill 255)
    (q/stroke 255)
    (q/line [0 0] [(* 1 (Math/sqrt 2.0)) 0])
    (q/with-translation
      [(* 1 (Math/sqrt 2.0)) 0]
      (q/no-fill)
      (q/stroke 0 255 255)
      (q/ellipse 0 0 2 2)
      (q/with-rotation
        [direction]
        (q/stroke 255 255 0)
        (q/begin-shape)
        (apply q/vertex [0 0])
        (apply q/vertex [strength 0])
        (q/end-shape)
        (q/with-translation
          [strength 0]
          (q/stroke 255 0 0)
          (q/ellipse 0 0 (* 2 rate) (* 2 rate)))))))

(defn draw-boid [{[pos [vx vy]] :state :keys [width height color] :as boid}]
  (let [tw (* 0.5 width) th (* 0.5 height)]
    (q/with-translation
      pos
      (q/with-rotation
        [(Math/atan2 vy vx)]
        ;debug wander
        (debug-wander boid)
        (q/with-rotation
          [(q/radians -90)]
          (q/no-fill)
          (apply q/stroke color)
          (q/triangle (- tw) (- th) 0 th tw (- th)))))))

(defn draw [{:keys [world flock]}]
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
      ;Draw all flock
      (doseq [boid flock] (draw-boid boid)))))