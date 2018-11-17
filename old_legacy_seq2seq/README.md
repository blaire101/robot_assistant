
## Environment

1. Tensorflow 1.4.0
2. nltk >>> nltk.download('punkt')

> nltk.tokenize.punkt 中包含了很多预先训练好的 tokenize模型

## 测试效果

根据用户输入回复概率最大的前beam_size个句子：

```bash
> hello
Replies --------------------------------------->
hello .
hi .
>  
how are you
Replies --------------------------------------->
fine .
>  
how old are you
Replies --------------------------------------->
thirty-five .
>  
hehe
Replies --------------------------------------->
yes .
what ?
>  
how are you
Replies --------------------------------------->
fine .
>  
what's your name?
Replies --------------------------------------->
javier rodriguez .
pinkus .
laura .
joad .
>  
```

## 使用方法

1. 载代码到本地（data文件夹下已经包含了处理好的数据集 159657 QA，所以无需额外下载数据集）
2. 训练模型，将 chatbot.py 文件的 decode参数 修改为 False，进行训练模型
3. 训练完之后（大概要21个小时左右的时间， 30个epoches, 电脑配置 CPU，16G内存)
4. 将 decode参数修改为 True 就可以进行测试了。输入 Query， 看 Response

> 这里还需要注意的就是要记得修改数据集和最后模型文件的绝对路径，不然可能会报错。
>
> 在 chatbot.py 文件的三处。好了，接下来就可以愉快的运行了

