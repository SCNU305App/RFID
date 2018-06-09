package Logic;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reborn.login.ActivityCollector;
import com.example.reborn.login.MenuActivity;
import com.example.reborn.login.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Add_book extends AppCompatActivity{
    private EditText name_add, code_add, concern_add, location_add, author_add;
    private Button add_book;
    private String str;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.add);
        init();//初始化
    }
    private void init(){
        name_add = (EditText) findViewById(R.id.name);
        code_add = (EditText) findViewById(R.id.code);
        concern_add = (EditText) findViewById(R.id.concern);
        location_add = (EditText) findViewById(R.id.location);
        author_add = (EditText) findViewById(R.id.author);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取时间
        str = formatter.format(curDate);
        //添加按钮监听
        add_book = (Button) findViewById(R.id.add);
        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strname = name_add.getText().toString();
                String strcode = code_add.getText().toString();
                String strconcern = concern_add.getText().toString();
                String strlocation = location_add.getText().toString();
                String strauthor = author_add.getText().toString();

                //判断是否为空
                if (strname.equals("")){
                    Toast.makeText(Add_book.this, "名称不能为空，请重新输入",
                            Toast.LENGTH_LONG).show();

                }else if (strauthor.equals("")){
                    Toast.makeText(Add_book.this, "作者不能为空，请重新输入",
                            Toast.LENGTH_LONG).show();

                }else if (strcode.equals("")){
                    Toast.makeText(Add_book.this, "编码不能为空，请重新输入",
                            Toast.LENGTH_LONG).show();

                }else if (strconcern.equals("")){
                    Toast.makeText(Add_book.this, "出版社不能为空，请重新输入",
                            Toast.LENGTH_LONG).show();

                }else if (strlocation.equals("")){
                    Toast.makeText(Add_book.this, "位置不能为空，请重新输入",
                            Toast.LENGTH_LONG).show();

                }else {
                    ContentValues values = new ContentValues();
                    values.put("bookname", strname);
                    values.put("author", strauthor);
                    values.put("code", strcode);
                    values.put("concern", strconcern);
                    values.put("location", strlocation);
                    Toast.makeText(Add_book.this, "图书添加成功",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Add_book.this, MenuActivity.class);
                    startActivity(intent);
                    ActivityCollector.finishAll();
                }
            }
        });
    }
}
