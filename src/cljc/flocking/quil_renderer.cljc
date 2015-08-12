(ns flocking.quil-renderer
  (:require [quil.core :as q #?@(:cljs [:include-macros true])]))

(defn draw-boid [{[px py vx vy] :state
                  { {:keys [wander-direction wander-strength wander-rate]} :wander } :behaviors
                  :keys [width height]}]
  (let [tw (* 0.5 width) th (* 0.5 height)]
    (q/with-translation
      [px py]
      (q/with-rotation
        [(Math/atan2 vy vx)]
        (q/fill 255)
        (q/stroke 255)
        (q/line [0 0] [(* 1 (Math/sqrt 2.0)) 0])
        (q/with-translation
          [(* 1 (Math/sqrt 2.0)) 0]
          (q/no-fill)
          (q/stroke 0 255 255)
          (q/ellipse 0 0 2 2)
          (q/with-rotation
            [wander-direction]
            (q/stroke 255 255 0)
            (q/line [0 0] [wander-strength 0])
            (q/with-translation
              [wander-strength 0]
              (q/stroke 255 0 0)
              (q/ellipse 0 0 (* 2 wander-rate) (* 2 wander-rate)))))
        (q/with-rotation
          [(q/radians -90)]
          (q/stroke 0 255 0)
          (q/triangle (- tw) (- th) 0 th tw (- th)))))))

(defn draw [{:keys [world boids]}]
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
      (q/stroke-weight (/ max-world-dim min-screen-dim))
      (doseq [boid boids] (draw-boid boid)))))