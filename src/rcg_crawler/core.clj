(ns rcg-crawler.core
  (:use [image-resizer.crop :as crop]
        [image-resizer.format :as format])
  (:require [clojure.java.io :as io]
            [image-resizer.core :refer :all]
            [image-resizer.util :as util])
  (:import javax.imageio.ImageIO))

(defn get-words [url]
  (re-seq #"[A-Z][a-z]+" url))

(defn find-rcg-url [html]
  (->> (re-find #"files.explosm.net/rcg/.+\.png" html)
       (str "http://")))
                          ;;0 245 490
(defn split-image [url]   ;;280 height
  (let [img (util/buffered-image (io/as-url url))]   ;;240 width
    [(crop-from img 0 0 240 280)
     (crop-from img 245 0 240 280)
     (crop-from img 490 0 240 280)]))

(defn get-data []
  (let [url (find-rcg-url (slurp "http://explosm.net/rcg"))]
    (map vector (get-words url) (split-image url))))


(defn write-file [pair]
  (let [[name image] pair
        file (io/as-file (str "../frames/" name ".png"))]
    (if (not(.exists file))
      (ImageIO/write image "png" file))))

(defn -main [& args]
  (while true (println "Captured" (count (filter identity (map write-file (get-data)))) "new frames.") (Thread/sleep 1000)))
