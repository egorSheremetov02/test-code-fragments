## Часть 1

Я обнаружил `19` вхождений. 

Для этого я использовал команду 

```
objdump -D /bin/grep
```
Оно выдало дизассемблированный исполняемый файл.

Далее я использовал утилиту `less`, с помощью неё я нашёл все строчки, который содержали `malloc` в качестве подстроки. Пришлось проигнорировать сам код функции `malloc`, все остальные вхождения вполне были вызовами соответствующей функции из `glibc`.

То есть, всё, что я по итогу сделал -- это ввёл в консоль следующую команду:

```
objdump -D /bin/grep | less
```
А дальше просто прошёлся по всем вхождениям, проверив, что слева от вхождения `malloc` написана инструкция `callq` (поскольку архитекрура 64-битная).

## Часть 2

Тут, в целом, достаточно ясная и понятная стратегия. Для того, чтобы этого добиться, можно получить `Constant pool`, в дальнейшем при нахождении очередного `invokevirtual` нам достаточно найти соответствующий номер в таблице, там будет `Methodref`, он будет содержать номер из `Constant pool`, который будет ссылаться на имя нужного класса.

Изначальная стратегия заключалась в том, чтобы использовать библиотку от `apache`, `bcel`. Она достаточно удобна, однако, к сожалению, не позволяет парсить сам байт-код. Но всё же сам код можно вычленить и некоторыми костылями достать имя класса, у которого вызывается виртуальный метод (впрочем, в джаве все методы вирутальные по умолчанию, поэтому это просто вызовы методов объекта). В итоге я её и использовал, однако не удалось сделать так красиво, как хотелось изначально(.

Как этим пользоваться: надо собрать эту штуку с помощью `gradle` и передать ей как-то аргументы командной строки. Конкретно я для этого использовал `gradlew`. Ещё небольшой, некритичный нюанс, заключается в том, что необходимо передавать абсолютный путь до файла. В итоге запуск в моём случае выглядел следующим образом: 

```
~/HSE/nir/test-code-fragments/InvokeVirtualClassesGradle$ ./gradlew run --args="/home/egor/HSE/nir/test-code-fragments/A.class"
```

