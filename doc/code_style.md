# 微逸输入法代码规范

前言
------
部分规范来自google java编程风格，感谢@hawstein的翻译

## 源文件基础
0.1 文件名
------
源文件以其最顶层的类名来命名，大小写敏感，文件扩展名为.java。

0.2 文件编码：UTF-8
----
源文件编码格式为UTF-8。

0.3 特殊字符
0.3.1 空白字符
除了行结束符序列，ASCII水平空格字符(0x20，即空格)是源文件中唯一允许出现的空白字符，这意味着：

所有其它字符串中的空白字符都要进行转义。
制表符不用于缩进。
0.3.2 特殊转义序列
对于具有特殊转义序列的任何字符(\b, \t, \n, \f, \r, ", '及\)，我们使用它的转义序列，而不是相应的八进制(比如\012)或Unicode(比如\u000a)转义。

0.3.3 非ASCII字符
对于剩余的非ASCII字符，是使用实际的Unicode字符(比如∞)，还是使用等价的Unicode转义符(比如\u221e)，取决于哪个能让代码更易于阅读和理解。

Tip: 在使用Unicode转义符或是一些实际的Unicode字符时，建议做些注释给出解释，这有助于别人阅读和理解。
例如：

String unitAbbrev = "μs";                                 | 赞，即使没有注释也非常清晰
String unitAbbrev = "\u03bcs"; // "μs"                    | 允许，但没有理由要这样做
String unitAbbrev = "\u03bcs"; // Greek letter mu, "s"    | 允许，但这样做显得笨拙还容易出错
String unitAbbrev = "\u03bcs";                            | 很糟，读者根本看不出这是什么
return '\ufeff' + content; // byte order mark             | Good，对于非打印字符，使用转义，并在必要时写上注释
Tip: 永远不要由于害怕某些程序可能无法正确处理非ASCII字符而让你的代码可读性变差。当程序无法正确处理非ASCII字符时，它自然无法正确运行， 你就会去fix这些问题的了。(言下之意就是大胆去用非ASCII字符，如果真的有需要的话)


## 一 命名规范：
	1. 包命名规范：全部小写，不使用下划线
	2. JAVA类命：ThisIsClass
	3. 接口：ThisIsImplement
	4. 成员变量：aMember
	5. 临时变量：随便
	6. 常量：全部大写加下划线区分单词
	7. 控件：类中控件名称必须与xml布局id保持一致。例如：android:id=="@+id/bt_set",在类中为private Button bt_set.
	8. 方法：aMethod
	9. 布局文件：全部小写，采用下划线命名法。例如：activity layout :{module name}_activity_名称
	10. 资源id命名：{view缩写}_{module_name}_{view的逻辑名称}。例如：Guide的界面布局：activity_guide


## 二 代码风格：

### 1. 大括号：

		if (hasMoney()) {

		} else {

		}

### 2.注释：实例变量：说明用途就行

			// 用户姓名
			private String userName
		必须对所有的类和接口进行说明。例如：


			/**
			* 引导安装Activity，
			* 调用时机：在用户第一次安装的时候调用
			*
			*/
			public class GuideActivity extends Activity
			{

			}

		必须对所有的方法进行注释说明。 例如:
			/**
         			* TODO
         			* 功能：从SharedPreference中加载键盘的尺寸信息
         			* 调用时机：初始化或旋转手机方向
         			*/
        			private void LoadKeyboardSizeInfoFromSharedPreference() {

			}
