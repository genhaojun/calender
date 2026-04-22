package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvExpression;  // 算式显示区域
    private TextView tvResult;      // 算数结果显示区域

    private String expression = "";      // 当前输入
    private boolean justCalculated = false; // 结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        // 所有按钮的点击事件
        int[] btnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9, R.id.btnDot,
                R.id.btnAdd, R.id.btnSub, R.id.btnMul, R.id.btnDiv,
                R.id.btnEqual, R.id.btnC, R.id.btnDel, R.id.btnPercent
        };
        for (int id : btnIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        String text = btn.getText().toString();

        switch (v.getId()) {
            case R.id.btnC:
                // C按钮
                expression = "";
                justCalculated = false;
                tvExpression.setText("");
                tvResult.setText("");
                return;

            case R.id.btnDel:
                // Backspace
                if (justCalculated) {
                    expression = "";
                    justCalculated = false;
                    tvExpression.setText("");
                    tvResult.setText("");
                } else if (expression.length() > 0) {
                    expression = expression.substring(0, expression.length() - 1);
                    tvExpression.setText(expression);
                }
                return;

            case R.id.btnEqual:
                // 结果
                doCalculate();
                return;

            default:
                // 数字和运算符按钮
                if (justCalculated) {
                    // 判断按的是运算符还是数字
                    if (isOperator(text)) {
                        // 运算符：用上次的结果接着算
                        justCalculated = false;
                        // expression里已经是上次的结果了，直接加运算符
                    } else {
                        // 按数字：重新开始
                        expression = "";
                        tvResult.setText("");
                        justCalculated = false;
                    }
                }
                expression += text;
                break;
        }

        // 更新上方算式显示jieguo
        tvExpression.setText(expression);
    }

    // 判断是不是运算符
    private boolean isOperator(String s) {
        return "+".equals(s) || "−".equals(s) || "×".equals(s) || "÷".equals(s);
    }

    // 计算并显示结果
    private void doCalculate() {
        if (expression.isEmpty()) return;

        try {
            // 把显示符号替换成可以计算的符号
            String expr = expression;
            expr = expr.replace("%", "/100.0");
            expr = expr.replace("×", "*");
            expr = expr.replace("÷", "/");
            expr = expr.replace("−", "-");

            double result = eval(expr);

            // 检查除数是否为0
            if (Double.isInfinite(result)) {
                tvExpression.setText(expression + " =");
                tvResult.setText("除数不能为0");
                expression = "";
                justCalculated = false;
                return;
            }

            // 格式化结果
            String resultStr;
            if (result == (long) result) {
                resultStr = String.valueOf((long) result);
            } else {
                resultStr = String.valueOf(result);
            }

            // 上方显示算式，下方显示结果
            tvExpression.setText(expression + " =");
            tvResult.setText(resultStr);

            // 保存结果，方便继续运算
            expression = resultStr;
            justCalculated = true;

        } catch (Exception e) {
            tvExpression.setText(expression + " =");
            tvResult.setText("错误");
            expression = "";
            justCalculated = false;
        }
    }

    // 简单表达式求值
    private double eval(String expr) {
        // 先算乘除
        while (expr.contains("*") || expr.contains("/")) {
            int idx = -1;
            char op = ' ';
            for (int i = 0; i < expr.length(); i++) {
                if (expr.charAt(i) == '*' || expr.charAt(i) == '/') {
                    idx = i;
                    op = expr.charAt(i);
                    break;
                }
            }
            double left = getNumBefore(expr, idx);
            double right = getNumAfter(expr, idx);
            double val = (op == '*') ? left * right : left / right;
            expr = replacePart(expr, idx, val);
        }
        // 再算加减
        String[] parts = expr.split("(?=[+-])");
        double result = 0;
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                result += Double.parseDouble(part);
            }
        }
        return result;
    }

    private double getNumBefore(String expr, int opIdx) {
        int start = opIdx - 1;
        while (start >= 0 && (Character.isDigit(expr.charAt(start)) || expr.charAt(start) == '.')) {
            start--;
        }
        return Double.parseDouble(expr.substring(start + 1, opIdx));
    }

    private double getNumAfter(String expr, int opIdx) {
        int end = opIdx + 1;
        while (end < expr.length() && (Character.isDigit(expr.charAt(end)) || expr.charAt(end) == '.')) {
            end++;
        }
        return Double.parseDouble(expr.substring(opIdx + 1, end));
    }

    private String replacePart(String expr, int opIdx, double value) {
        int start = opIdx - 1;
        while (start >= 0 && (Character.isDigit(expr.charAt(start)) || expr.charAt(start) == '.')) {
            start--;
        }
        int end = opIdx + 1;
        while (end < expr.length() && (Character.isDigit(expr.charAt(end)) || expr.charAt(end) == '.')) {
            end++;
        }
        return expr.substring(0, start + 1) + value + expr.substring(end);
    }
}
