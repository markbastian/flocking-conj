(ns flocking.launcher
  (:gen-class)
  (:require [flocking.game-launcher :as fl]))

(defn -main [& _] (fl/launch-sketch { :width 800 :height 600 }))
