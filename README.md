# 第四单元总结

## 总

本部分是2019年北航面向对象（OO）课程第四单元作业（UML）的总结。两次作业的要求大致如下：

- 第一次作业：实现一个UML类图解析器，可以通过输入各种指令来进行类图有关信息的查询。
- 第二次作业：扩展类图解析器，使得可以支持对UML状态图和顺序图的解析，并可以通过输入相应的指令来进行相关查询。

源代码及项目要求均已发布到 [github](https://github.com/LutingWang/UML_Parser) ，读者可以下载检查。以下将对这一单元作业进行简单总结。

## UML

本次作业中使用`StarUML`绘制`UML`类图，为方便后文叙述，本节首先介绍`StarUML`中涉及到的部分元素及其组织结构。本次作业共涉及三种`UML`图，分别是类图、顺序图与状态图。

在`StarUML`的`mdj`文件中，有一个顶层`Project`元素，是所有`UML`图的父节点。每个`UML`图单独作为一个节点存在，其中类图的类型是`UMLModel`，顺序图的类型是`UMLCollaboration`，状态图的类型是`UMLStateMachine`。

### UML 类图

类图是`UML`最常用的图之一，用于描述面向对象程序设计中，类、接口等结构之间的关系，如图

![model](https://img2018.cnblogs.com/blog/1615581/201906/1615581-20190622151520677-1337050313.png)

类图中涉及到以下几种类型的对象

- UMLClass 如图中`Class1`，代表类
  - UMLOperation 如图中`Operation1`，代表类中方法
    - UMLParameter 如图中`a`，代表方法的参数与返回值
  - UMLAttriubte 如图中`Attribute1`，代表类中属性
- UMLInterface 如图中`Interface1`，代表接口
- UMLGeneralization 代表类或接口的继承关系
- UMLAssociation 代表类的关联关系
  - UMLAssociationEnd 代表关联端

### UML 时序图

时序图可以用于描绘多个类或线程之间的协作关系，如图

![sequence](https://img2018.cnblogs.com/blog/1615581/201906/1615581-20190622151529987-1735855627.png)

其中涉及到的对象有

- UMLLifeline 如图中`Lifeline1`，表示一个线程
- UMLMessage 如图中`Message1`，表示一个消息

### UML 状态图

状态图可以用于描绘一个类的对象的状态转移，如图

![statemachine](https://img2018.cnblogs.com/blog/1615581/201906/1615581-20190622151539732-847057244.png)

其中涉及到的对象有

- UMLPseudoState 表示 initial state 或中间状态
- UMLState 如图中`State1`，表示一个有名状态
- UMLFinalState 表示终止状态
- UMLTransition 表示状态转移

## 架构设计

本次作业的核心思路是封装。由于程序的输入输出由助教团队下发的官方包完成，我们只需要实现接口中的特定函数即可。但官方包中使用的数据类型是与`mdj`文件一致的，不适合在业务逻辑中使用。因此，在本单元的两次作业中，最重要的部分是提取出官方包使用的数据类型间的组织形式，并将其搭建为图或树，以便后续查询。

### 第十三次作业

第十三次作业中仅涉及到类图的解析，相对比较简单。

![hw1](https://img2018.cnblogs.com/blog/1615581/201906/1615581-20190622151439494-1435099025.png)

定义类`MyUmlOperation`，`MyUmlClass`，`MyUmlInterface`，`MyUmlAssociation`，`MyUmlDiagram`用于描述类图的组织关系。其中`MyUmlDiagram`是类图的顶层类，包含一个类图中的所有类和接口及其关联关系。考虑到类和接口的共性，抽象出`MyUmlStruct`类作为其公共父类，处理**方法**和**关联**。

考虑到查询操作大多用名称完成，而不同元素的名称可以相同，因此定义数据结构`MyMap`。这一数据结构可以存储元素和名称的对应关系，提供依据名称查询的接口，当查询不到或查询到多个时抛出异常。

类`MyUmlInteraction`用于实现接口，对`MyUmlDiagram`实现的方法进行调用。

### 第十四次作业

第十四次作业在上次作业的基础上加入了时序图和状态图的解析，而类图部分几乎不变

![hw2](https://img2018.cnblogs.com/blog/1615581/201906/1615581-20190622151506925-1985845234.png)

本次作业可以分为并行的三个部分`MyUmlClassModel`，`MyUmlStateMachineModel`以及`MyUmlInteractionModel`。其中`MyUmlDiagram`表示类图，其中大部分功能已经在上次作业中实现。本次作业主要实现`MyUmlStateMachineModel`和`MyUmlInteracitonModel`的业务逻辑。

实现时，`MyUmlStateMachine`和`MyUmlInteraction`用于描述单个`UML`图。为了减轻`MyUmlGeneralInteraction`接口的压力，加入`MyUmlStateMachineModel`和`MyUmlInteractionModel`两个中间类以组织同一类型的多个`UML`图。由于有关这两种图的查询操作都比较简单，只需要在构造时将不同类型的元素分别加入`MyMap`中即可。

较难实现的部分是类图的有效性检查，因为需要对多个类或接口的总体关系进行考察。但好在上次作业实现时已经搭建起了图结构，有效性检查可以在单个类或接口中实现，而不必完全交给`Diagram`，从而降低了耦合。最终的实现方法是，`MyUmlClass`和`MyUmlInterface`中分别实现针对该类或接口的检查方法；`MyUmlClassModel`中遍历所有类和接口对检查方法进行调用。这样当检查点变化时，也只需要更改对应的函数，不需要修改其他无关部分。

# 课程总结

经过了四个单元、15次作业，本学期的面向对象课终于迎来了尾声。不得不说，一学期的课程学习让我以一种神奇的经历加深了对于面向对象的理解。下面对本学期的课程学习做一个总结

## 历次作业总结

### 架构设计

架构是面向对象课程学习的重点，也是课程组重点训练我们的地方。作业中虽然没有任何一次作业是针对架构进行训练的，但是每个单元都会有助教对于架构设计的建议，而我也确实感觉到自己的架构设计能力在一点点提高。到了本单元作业，我已经可以很轻松的设计出一个合适的架构了。

#### 第一单元 多项式求导

这个单元中，我对于面向对象的理解尚不深入。由于面向过程的思维仍然占据主导，我对编程的目标拘泥于代码复用。尽管我尝试着对架构进行设计，但最终证明抽象出来的类大多是可有可无的。

举个例子来说，在第一次作业中，我引入了`Derivable`接口作为所有元素的共同接口，以便传入参数时不需要重载函数。但是推进到第三次作业时，我在`Poly::add(Derivable)`或是`Item::mult(Derivable)`方法中全部使用`instanceof`关键字判断传入参数的具体类型。不同类型的参数需要调用不同的私有函数，不仅没有达到代码复用的目的，反而为编程和调试增加了难度。

```java
public Poly mult(Derivable derivable) {
    if (derivable instanceof Item) {
    	return mult((Item) derivable);
    }
    if (derivable instanceof Poly) {
    	return mult((Poly) derivable);
    }
    throw new ClassCastException();
}
```

经过这一单元的作业，我认识到架构设计并不是简单的抽象共性。他涉及到可拓展性、多态性等等许多需要考虑的方面。在得知具体需求之前，我们很难设计出一个可以应对所有情况的架构。但我们可以尽量把架构的可扩展性提高，减少迭代时需要更改的部分。这是我对于架构设计最初步的认识，**架构设计的目标是尽量提高代码复用，将共性逻辑提取出来，以便在需求更改时修改尽量少的代码**。

#### 第二单元 电梯

第二单元作业引入了多线程设计。这个单元中，应对多线程的冒险总是让我疲惫不堪。我尝试参考显示生活的实际情景去搭建架构，将电梯、控制器、人群等分别抽象为线程。其结果就是这一单元的第二次作业我已经引入了多达 10 个线程。尽管线程增加后程序的并行性能提升，使程序效率略有提高。但是解决程序安全性问题耗费了我大量的经历，导致调度算法的设计不够精致，最终程序运行效率的微小提升远不足以抵消算法缺陷导致的延误。

而为了减少线程安全问题发生的机率，我大量使用内部类，以访问某类的隐藏属性。

```java
public class Elevator implements AutoStart {
    private enum Status { OPEN, NULL, RUNNING }
    
    private enum Dir { STOP, UP, DOWN, NULL }
    
    private final class PeopleIn extends People implements AutoStart {
		// ...
    }
    
    private final class SubManager extends Manager implements AutoStart {
		// ...
    }
}
```

事实上，使用内部类的设计是符合现实情况的。但是在编程时，复杂的结构常常让我无从下手。类与类的耦合过高，导致作业推进时，架构往往要做出翻天覆地的改变。

由此，我逐步发现我在编程时过于注重代码的简洁性，而忽略了耦合。在第一单元中，不同元素之间存在固有的聚合关系，即使耦合度稍高也无关紧要。但本单元中，不同线程之间需要尽量降低耦合。但是因为我开启的线程过多，许多线程是由本就耦合度很高的一段代码中拆分而来，因此线程间的通讯十分频繁。这就导致了类与类之间的耦合明显提高，完全没有扩展空间。

进行总结时，我认识到了这个问题。在其后的作业中，我放松了对于代码复用性的要求，转而要求**尽量降低类与类之间的耦合**。实际上，追求代码简洁性的原意也是降低耦合。但如果过分注重简洁而忽略了代码的逻辑关系，有可能适得其反。

#### 第三单元 规格化设计

第三单元作业注重`JML`的应用，因此并没有涉及太多规格设计，在此掠过不提。

#### 第四单元 UML 解析

本单元作业的规格设计显得比较简单，也许是经过了前面三个单元的训练后，我对于规格的掌握有了进步。总之在拿到作业后，我的第一想法是对官方包中的数据类型进行封装。在编程时发现`HashMap`并不能满足我的使用需求，于是自己写了`MyMap`作为底层数据结构。

总的来说，我认为本单元作业是对前期作业的总结。在面对一个特定的需求时，我可以将其划分模块，自顶向下进行设计。任何一个架构设计都不可能再一开始就具体入微。因此再确定了模块之后，我会针对每个模块在进行设计，然后再编码过程中修改设计，直到模块完成。

或许我对于架构的理解还不够深入，但我已经可以有意识的在动手编程前对任务进行拆分与分析。我想这学期的课程多少教会了我设计与学习设计架构的基本知识。

### 面向对象方法理解

在`Java`之前，已经在`Python`和`Matlab`中接触过面向对象编程，但是实际意义上的面向对象开发是从这个学期开始的。说实话，我认为面向对象编程和面向过程编程的区别并不大，主要是思考问题的角度不同。一旦习惯了以对象为单位思考问题，剩下的就是语法问题了。至于思维方式，面向对象编程本质上是面向对象编程的扩充。以`Java`为例，每个方法实际上都是面向过程的，面向对象只是提供了属性和方法的组织以及继承等特性。

只要拥有了面向对象的思维方式，完成作业就变成了对于实际问题的抽象。以第二单元作业为例，电梯、控制器等都是现实生活中存在的模块，程序只是将其用代码进行描述。四次作业基本都是这样，因此在本学期中并没有什么演进。

### 测试方法理解与实践

测试一直一来都是编程的对立面，但是在其他课程中没有特意的训练。本学期的作业中由于需要保证程序正确性，不得不进行大量测试。

#### 第一单元 多项式求导

本单元作业中，我最开始使用的是惯用的测试方法，也就是没有方法，**随机测试**。但是这样测试的效果并不好，因为第一次作业我的代码就被人`hack`了`\f`的处理。

于是我开始学习使用**单元测试**的方法。但是单元测试的测试集仍然是自己编写的，不能保证做到覆盖。而且针对性太强，不能用于测试他人代码。

与此同时，周围的同学开始编写脚本和**评测机**进行测试，于是我也使用了这种方法。这种方法显然是效果最好的，但是也是最不能体现测试方法的。

在这个单元，我认为**测试就是用于发现和排除程序中的`bug`的方法**。尽管我尝试过编写完备的测试集，但是自己编写的测试集很难测试出自己的`bug`。因此，我认为测试应该有其他人或程序进行。

#### 第二单元 电梯

由于本单元涉及到多线程编程，测试难度增大。测试集只能保证程序在一般情况下表现正常，却难以进行覆盖测试。而电梯的调度算法又因人而异，相同的输入会产生不同的输出，因此自动测试也难以进行。所以在这个单元中我的测试量大幅减少，只是进行最基本的正确性检查。这也直接导致第八次作业中，我的程序出现严重错误。这次作业让我认识到，无论需求多么简单或是测试多难，测试还是需要进行的。测试越是充分，程序的正确性就越有保证。

#### 第三单元 规格化设计

规格的出现让我对自动测试有了全新的认识。过去我在编写评测机的时候都是随机生成测试数据，通过工作量来保证程序的正确性。而`JML`则不同，他通过生成完备的测试集来进行测试。这样可以从理论上证明程序的正确性，但代价是需要更长的时间编写需求。而且对于大型程序，`JML`的表现并不好。在很多情况下，`JML`描述的规范并不能正确的被识别。因此，在测试时，我还是会首先选择随机生成样例或手写测试集。

不过这个单元让我意识到，**测试可以是证明**，即用有限的测试去证明程序是正确度。

#### 第四单元 UML 解析

本单元在测试方法上不超出以上范围，不再细说。

## 改进建议

其实我认为课程组已经做得很好了，但是一定要找缺点的话，可能以下三点是明年 OO 课可以改进的方向。

### 尽量避免与面向对象无关的任务

主要针对前两单元。我并不反对在面向对象中加入算法的内容，但是我认为可以降低其比重。电梯作业中，优化的分数占到单次作业的 $15\% \sim 20 \%$，已经不只是学有余力的同学需要考虑的问题了。而且课程组的评分方法是以全体同学的均分为基准的，这就导致大家将注意力放在算法上，而非面向对象设计。

我认为，即使面向对象设计的好坏难以评价，仍然应该以此为主。而如果以算法为评分依据的话，难免会降低面向对象学习的效率。

### 时间安排提前安排好

本学期的时间安排总体比较稳定。但是中间经历了一次假期，导致 OO 作业延时；期末时 OO 作业的安排也比较混乱。我猜测课程组在开学前并没有考虑到这些变化，否则应该会提前做好计划，及时通知我们。校历可以提前给出放假安排，就是说课程组也可以提前规划好。上次作业延时的结果是一周有两次 OO 作业，尽管大家还是按时完成，但是必然有些疲惫。因此我认为明年课程组可以提前规划好每次作业的时间安排，尽量不要调整。

### 理论课存在感不强

一旦课程设计或是作业太难，理论课的存在感总是会被削弱。因为大家完全可以也必须通过自学来掌握所需知识。我能感受到课程组尽力将理论课和实验课、作业联系起来。但是就我个人感觉而言，理论课的作用仍然不大。事实上，从第一周的研讨课上，就有同学在讲继承、多态的内容，因此我猜测大部分同学其实已经自学过相关知识了。而理论课还要等到几周之后才涉及到相关内容，显然节奏有些慢了。

仅仅是提出一个问题，至于能不能解决、如何解决，就要取决于课程组了。

## 课程收获

面向对象这门课，让我有一种课程设计的感觉。尽管课上实验并没有与作业挂钩，但课下作业还是给我一种紧张感，好像每次的强测互测就是课上测试一样。说真的，我想要感谢每位助教的工作。无论是指导书、评测机或是官方代码，几乎没有出过差错。这对于一个还在变革中的课程来说是十分可贵的。

我想说，我收获了很多。这门课和 OS 在同期进行，目的都是让我们开发工程。但 OO 是我第一次真切的有一种软件开发的感觉。其实在开课以前，我已经接触过面向对象，并且写过很多面向对象的代码。因此我以为这门课对于我来说会很简单。但是在拿到第一单元的第三次作业之后我便不再这么想了，因为我花了很长时间去思考算法、架构。我时常会思考为什么我掌握了面向对象，但这门课对于我来说还是这么难。一直一来我的结论是：因为作业的侧重点太过严重的偏离了面向对象。

但是随着课程深入，我的想法开始改变，我发现作业的安排有其合理之处。面向对象是针对编程而言，离开具体情景，面向对象本身就是没有意义的。我之所以会觉得难，一部分原因是我的程序设计水平有待提高，但刚主要的原因是我对于面向对象的理解还不够深入。我曾经单纯的认为面向对象就是一种思维方式，就像任何一本面向对象程序设计书的前言中写道的那样。我以为只要我按照面向对象的角度去思考问题，就是掌握了面向对象。但事实上，面向对象所包含的内容远比我想象中要多。不仅有前面提到的架构、测试，更包括了封装、优化等等。

面向对象的出现重新定义了我曾经熟悉的许多名词。除了那些具体的语法、编程技巧以外，我最大的收获也许是更加适应新的变化。计算机科学是一个庞杂的学科，他的每个分支都在经历着飞快的变革。面向过程到面向对象的变化看起来不大，但是却影响到了我原有认识的几乎所有方面。以后面对一个新的概念的时候，可能我会做好更充分的准备，去迎接一次颠覆性的改变。