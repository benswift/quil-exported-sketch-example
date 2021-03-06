(ns clojure-processing-sketch-example.sketches
  (:import processing.core.PApplet Jukebox)
  (:require [clojure.java.shell :refer [sh]]))

(def sketches
  (atom []))

(def current-sketch
  (atom {}))

(defn current-idle-time []
  (/ (- (System/currentTimeMillis) (:time @current-sketch)) 1000.0))

(defn touch-current-time
  "'touch' sketch with current time"
  []
  (swap! current-sketch assoc :time (System/currentTimeMillis)))

(defn run-papplet [papplet]
  (PApplet/runSketch
      (into-array String ["mysketch" "--present"])
      papplet)
  papplet)

(defn sketch-for-uid [uid]
  (first (filter  #(= (:uid %) uid) @sketches)))

(defn kill-current []
  (sh "killall" "tmux"))

(defn start
  ([]
   (start (->> @sketches (map :uid) rand-nth)))
  ([uid]
   (sh "tmux" "new" "-ds" "current-sketch" "processing-java" (str "--sketch=" (System/getProperty "user.dir") "/processing/" uid) "--present")
   (reset!
    current-sketch
    {:uid uid
     :time (System/currentTimeMillis)})))

(defn jukebox-proxy []
  (fn []
    (proxy [Jukebox] []
      (switchToSketch [uid]
        (println "switching to " uid)
        (start uid))
      (handleKeyEvent [event]
        (touch-current-time)
        ;; Ctrl+Alt+Q to quit
        ;; (when (and (= (.getKeyCode event) 157)
        ;;            (.isControlDown event)
        ;;            (.isAltDown event))
        ;;   (kill-current)
        ;;   (System/exit 0))
        )
      (handleMouseEvent [event]
        (touch-current-time)
        (proxy-super handleMouseEvent event)))))

(defn start-jukebox
  ([]
   (run-papplet ((jukebox-proxy)))
   (swap! current-sketch assoc :time (System/currentTimeMillis))))

(defn gallery-loop [idle-time]
  (loop []
    (when (> (current-idle-time) idle-time)
      (kill-current)
      (start))
    (Thread/sleep 10000)
    (recur)))

(defn init []
  (reset! sketches
          [{:uid "u1111111" :weight 1}
           {:uid "u2222222" :weight 1}
           {:uid "u3333333" :weight 1}])
  (reset! current-sketch {:uid "uXXXXXXX" :time (System/currentTimeMillis)})
  (start-jukebox))
