(ns hospital.logic-test
  (:use clojure.pprint)
  (:require [clojure.test :refer :all]
            [hospital.logic :refer :all]
            [hospital.model :as h.model]))

(deftest cabe-na-fila?-test

  ; boundary tests
  ; exatamente na borda e one off, -1, +1, <=, >=, =

  ; checklist na minha cabeca, poderia ser um checklist no papel

  ; borda do zero
  (testing "Que cabe numa vila fazia"
    (is (cabe-na-fila? {:espera []} :espera)))

  ; borda do limite
  (testing "Que não cabe na fila quando a fila está cheia"

    ; é de simples leitura pois é sequencial
    ; mas a desvantagem é que podemos errar em fazer coisas sequenciais
    ; (is (not (cabe-na-fila? {:espera [1 2 3 4 5]}, :espera)))

    ; não precisa ser sequencial e no mundo real não é
    ; portanto faca testes que não são sequencias!
    (is (not (cabe-na-fila? {:espera [1 5 37 54 21]}, :espera)))
    )

  ; one off da borda do limite pra cima
  (testing "Que não cabe na fila quando tem mais do que uma fila cheia"
    (is (not (cabe-na-fila? {:espera [1 2 3 4 5 6]}, :espera))))

  ; dentro das bordas
  (testing "Que cabe na fila quando tem gente mas não está cheia"
    (is (cabe-na-fila? {:espera [1 2 3 4]}, :espera))
    (is (cabe-na-fila? {:espera [1 2]}, :espera)))

  (testing "Que não cabe quando o departamento não existe"
    (is (not (cabe-na-fila? {:espera [1 2 3 4]}, :raio-x)))))

(deftest chega-em-test

  (let [hospital-cheio {:espera [1 35 42 64 21]}]

    (testing "aceita pessoas enquanto cabem pessoas na fila"
      ; implementação ruimi pois testa que escrevemos o que escrevemos.
      ; isto é, testa que erramos o que erramos, e que acertamos o que acertamos
      ;(is (= (update {:espera [1, 2,]} :espera conj 5)
      ;       (chega-em {:espera [1, 2]}, :espera, 5)))

      (is (= {:espera [1, 2, 3, 4, 5]}
             (chega-em {:espera [1, 2, 3, 4]}, :espera, 5)))

      ; teste não sequencial
      (is (= {:espera [1, 2, 5]}
             (chega-em {:espera [1, 2]}, :espera, 5))))


    ;(is (= {:hospital {:espera [1, 2, 3, 4, 5]}, :resultado :sucesso}
    ;       (chega-em {:espera [1, 2, 3, 4]}, :espera, 5)))
    ;
    ;; teste não sequencial
    ;(is (= {:hospital {:espera [1, 2, 5]}, :resultado :sucesso}
    ;       (chega-em {:espera [1, 2]}, :espera, 5))))

    (testing "não aceita quando não cabe na fila"
      ; verificando que uma exception foi jogada
      ; código clássico horrível. usamos uma exception GENERICA.
      ; mas qualquer outro erro generico vai jogar essa exception, e nós vamos achar que deu certo
      ; quando deu errado
      (is (thrown? clojure.lang.ExceptionInfo
                   (chega-em hospital-cheio, :espera 76)))

      ; mesmo que eu escolha uma exception do genero, perigoso!
      ;(is (thrown? IllegalStateException
      ;             (chega-em hospital-cheio, :espera 76)))

      ; outra abordagem, do nil
      ; mas o perigo do swap, teriamos que trabalhar em outro ponto a condicao de erro
      ;(is (nil? (chega-em hospital-cheio, :espera 76)))

      ; outra maneira de testar
      ; onde ao inves de como Java, utilizar o TIPO da exception para entender
      ; o TIPO (outro tipo) de erro que ocorreu, estou usando os dados da
      ; exception para isso
      ; menos sensivel que a mensagem de erro (mesmo que usasse regex)
      ; mas ainda é uma validacao trabalhosa
      ;(is (try
      ;      (chega-em hospital-cheio, :espera 76)
      ;      false
      ;      (catch clojure.lang.ExceptionInfo e
      ;        (= :impossivel-colocar-pessoa-na-fila (:tipo (ex-data e)))
      ;           )))

      ;(is (= {:hospital hospital-cheio, :resultado :impossivel-colocar-pessoa-na-fila}
      ;       (chega-em hospital-cheio, :espera 76)))
      )
    ))

(deftest transfere-test
  (testing "aceita pessoas se cabe"
    (let [hospital-original {:espera [5] :raio-x []}]
      (is (= {:espera []
              :raio-x [5]}
             (transfere hospital-original :espera :raio-x)))
      )

    (let [hospital-original {:espera (conj h.model/fila-vazia 51 5) :raio-x (conj h.model/fila-vazia 13)}]
      (pprint (transfere hospital-original :espera :raio-x))
      (is (= {:espera [5]
              :raio-x [13 51]}
             (transfere hospital-original :espera :raio-x)))
      )

    )

  (testing "recusa pessoas se não cabe"
    (let [hospital-cheio {:espera [5] :raio-x [1 2 53 42 13]}]
      (is (thrown? clojure.lang.ExceptionInfo
             (transfere hospital-cheio :espera :raio-x)))
      )
    ))

























