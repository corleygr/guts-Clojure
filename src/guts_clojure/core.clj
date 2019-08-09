(ns guts-clojure.core
  (:gen-class))

(def suits ["H" "D" "C" "S"])
(def ranks ["2" "3" "4" "5" "6" "7" "8" "9" "10" "J" "Q" "K" "A"])

(def deck-of-cards 
  (flatten (for [suit suits 
                 rank ranks] 
           [(str rank suit)])))

(defn pair-bonus
  [rank1 rank2]
  (if (= rank1 rank2)
    (* (count ranks ) 2)
    0))

(defn card-value
  [card]
  (.indexOf ranks (str (first card))))

(defn hand-value
  [card1 card2]
  (+ (pair-bonus (first card1) (first card2)) 
     (card-value card1)
     (card-value card2)))

(defn deal-card 
  [player card]
  (update player :hand #(conj % card)))

(defn player-list 
  [num-players wallet] 
  (mapv (fn [n] {:name (str "player-" n) :wallet wallet :hand [] }) (rest (range (+ 1 num-players)))))

(defn collect-ante
  [ante pot players]
  (let [players-in-game (filter #(> (- (:wallet %) ante) 0) players)]
     [(+ pot (* ante (count players-in-game))) (map #(update % :wallet - ante) players-in-game)]))

(defn deal-hands
  [deck players]
  (loop [cards deck
         ps players
         result []]
    (if ps
      (let [p (first ps)
            c (first cards)]
        (recur (next cards) (next ps) (conj result (deal-card p c))))
      result)))

(defn play-round
  [deck ante pot players]
  (let [ [pot players] (collect-ante ante pot players)]
    (deal-hands deck-of-cards  players)))

(defn collect-game-start-data
  []
  (println "Welcom to Guts!!")
  (println "How many opponents?")
  (def num-opponents (read-line))
  (println "How much in each wallet?")
  (def wallet (read-line))
  (println "Ante for each hand?")
  (def ante (read-line))
  {:opponents num-opponents :wallet wallet :ante ante})

(defn -main
  []
  (def game-options (collect-game-start-data))
  (def game-players (player-list (Integer/parseInt(:opponents game-options)) (Float/parseFloat(:wallet game-options))))
  (play-round deck-of-cards 1 0 game-players))

