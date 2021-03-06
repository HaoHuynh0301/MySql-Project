import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import com.haothuan.Main_project.*;
import com.sun.jdi.Value;

//CHANGE YOUR DATABASE PASSWORD IN CONNECT DATA FUNCTION

public class Screen extends JFrame{
    private JPanel pannel_main;
    private JTextField txt_email_input;
    private JPasswordField txt_pass_input;
    private JButton btnLogin;
    private JList list_product;
    private JTextField txt_product_price;
    private JButton btn_Order;
    private JTextField txt_Quantity;
    private JTextField txt_Comment;
    private JButton btn_signup;
    private JLabel txt_change_email_login;
    private JLabel txt_password_change_login;
    private JComboBox combobox_Method;
    private JList list_History;
    private JButton btn_Back;
    private JTextField txt_discount_code;
    private Icon icon = new ImageIcon("/Users/macos/Documents/Java Project/Data/Images/searchinglogo.png");
    private JTextField txt_find;
    private JButton btn_edit;
    private JPanel pannel_login;
    private JButton btn_Logout_2;
    private JButton btn_admin;
    private JButton btn_reduce_product;
    private JButton btn_add_product;
    private JLabel txt_Text_Quantity;
    private JLabel txt_Text_edit_product_list;
    private JScrollPane pannel_listhistory;
    private JComboBox comboBox_sort;
    private JButton btn_addtofavorite;
    private JButton btn_Find;
    private JButton btn_admin_edit;
    private JScrollPane pannel_croll_method;
    private JLabel txt_text_change_dis_rename;
    private JLabel txt_change_Comment;
    private JPanel pannel_button;
    private JLabel txt_text_change_edit;
    private JLabel txt_text_change_signup;
    private JButton btn_text;
    private JButton btn_Submit;
    private Functions function=new Functions();
    private int temp_product_id_1;

    //Customer's Point
    private int CUSTOMER_POINT;

    //Limit Point
    private int MAX_POINT=10;

    //Get now date
    long millis=System.currentTimeMillis();
    Date date= new Date(millis);

    //Order ID when select history
    private static String ORDER_ID;

    //Customer_ID
    private static String Customer_ID;

    //Flag for admin mode
    private boolean FLAG_admin=false;

    //Flag for sort
    private boolean FLAG_sort=true;

    //Flag for Edit Button
    private boolean FLAG_edit=true;

    //Flag for Order Button
    private boolean FLAG_order=true;

    //Flag for button Sign up
    private boolean FLAG=true;

    //Components for ComboBox_sort
    ArrayList<String> arr_sort_code;
    int Sort_Type_Code=0;

    //Components for ComboBox_payment_method
    static ArrayList<Customer_payment_method> arr_method;
    private static Customer_payment_method method;

    //Components for JList History
    DefaultListModel<String> model_list_history;
    private static ArrayList<Custumer_order> arr_history;
    private static Custumer_order customer_history;

    //Components for Jlist Products
    DefaultListModel<String> model_temp;
    DefaultListModel<String> model;
    private static ArrayList<Product> arr_Products;

    //Components for User Login
    private static ArrayList<User> arr_Mysql;
    private static Product product=null;
    private static User mysql=null;

    //Components for Customer Order


    private static Connection conn;

    public Screen() {
        super("CoffeeShop Management");
        this.setContentPane(this.pannel_main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        //Declare
        btn_admin_edit.setVisible(false);
        btn_Back.setVisible(true);
        btn_addtofavorite.setVisible(false);
        btn_edit.setVisible(true);
        btn_Logout_2.setVisible(true);
        btn_add_product.setVisible(false);
        btn_reduce_product.setVisible(false);
        list_product.setVisible(false);
        btn_Order.setVisible(false);

        //Setup for insert into comboBox sort
        arr_sort_code=new ArrayList<>();

        //Setup for insert into ComboBox Method
        arr_method=new ArrayList<>();
        method=null;

        //Customer ID
        Customer_ID="";

        //Setup for add net Customer_order_products
//        COP=new Customer_order_products();

        //Setup for inserting elements into JList History
        model_list_history=new DefaultListModel<>();
        customer_history=null;

        //Setup for inserting emelents into Jlist
        model_temp=new DefaultListModel<>();
        model = new DefaultListModel<>();

        //Setup for inserting users for logining
        arr_Mysql=new ArrayList<>();
        User mysql=null;

        //Button Back Event
        btn_Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt_change_email_login.setText("Email");
                txt_password_change_login.setText("Password");
                txt_email_input.setText("");
                txt_pass_input.setText("");
                btn_admin.setVisible(true);
                btn_signup.setVisible(true);
                btn_edit.setVisible(true);
                btnLogin.setVisible(true);
            }
        });

        //Button Add Product Event
        btn_add_product.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var temp_new_product_id=txt_Quantity.getText().toString();
                if(txt_product_price.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(pannel_main, "Input product's price!!!");
                }
                else {
                    var temp_new_product_price=Integer.parseInt(txt_product_price.getText());
                    var temp_product_type_code= generateMyBigNumber(20);
                    PreparedStatement stmt= null;
                    try {
                        stmt = (PreparedStatement) conn.prepareStatement("Call insert_product(?, ?, ?)");
                        stmt.setInt(1, temp_new_product_price);
                        stmt.setString(2, String.valueOf(temp_product_type_code));
                        stmt.setString(3, temp_new_product_id);
                        var rs = stmt.executeUpdate();
                        if(rs==1) {
                            JOptionPane.showMessageDialog(pannel_main, "Insert Succeed!!!");
                            model.clear();
                            connectData();
                            for(Product product : arr_Products) {
                                model.addElement(product.getProduct_id());
                            }
                            list_product.setModel(model);
                            txt_Quantity.setText("");
                            txt_product_price.setText("");
                        }
                        else {
                            JOptionPane.showMessageDialog(pannel_main, "Error!! Try again");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }); //Done

        //Button Reduce Product Event
        btn_reduce_product.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var temp_reduce_product_name=txt_Quantity.getText().toString();
                PreparedStatement stmt= null;
                try {
                    stmt = (PreparedStatement) conn.prepareStatement("SELECT * FROM product where product_id=?");
                    stmt.setString(1, temp_reduce_product_name);
                    var rs = stmt.executeQuery();
                    if(rs.next()) {
                        PreparedStatement stmt_2= null;
                        stmt_2 = (PreparedStatement) conn.prepareStatement("CALL reduce_product(?)");
                        stmt_2.setString(1, temp_reduce_product_name);
                        int rs_2=stmt_2.executeUpdate();
                        if(rs_2!=-1) {
                            JOptionPane.showMessageDialog(pannel_main, "Delete Succeed");
                            model.clear();
                            connectData();
                            for(Product product : arr_Products) {
                                model.addElement(product.getProduct_id());
                            }
                            list_product.setModel(model);
                            txt_Quantity.setText("");
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(pannel_main, "Error");
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }); //Done

        //Button admin edit event
        btn_admin_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement stmt=(PreparedStatement) conn.prepareStatement("Call edit_product_infor(?, ?, ?)");
                    stmt.setString(1, arr_Products.get(list_product.getSelectedIndex()).getProduct_id());
                    stmt.setInt(2, Integer.parseInt(txt_product_price.getText()));
                    stmt.setString(3, txt_discount_code.getText());
                    var rs=stmt.executeUpdate();
                    if(rs!=-1) {
                        model.clear();
                        connectData();
                        for(Product product : arr_Products) {
                            model.addElement(product.getProduct_id());
                        }
                        list_product.setModel(model);

                        JOptionPane.showMessageDialog(pannel_main, "Edit product information succeed!");
                        txt_discount_code.setText("");
                        txt_product_price.setText("");

                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }); //Done

        //Button Admin Event
        btn_admin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp_email=txt_email_input.getText();
                String temp_pass=txt_pass_input.getText();
                try {
                    //Test email and password for logging
                    PreparedStatement stmt= (PreparedStatement) conn.prepareStatement("Select * from Customer where email_login=? and password_login=?");
                    stmt.setString(1, temp_email);
                    stmt.setString(2, temp_pass);
                    ResultSet rs=stmt.executeQuery();
                    if(rs.next()) {
                        System.out.println("Done");
                        txt_text_change_dis_rename.setText("Rename");
                        list_product.setVisible(true);
                        pannel_login.setVisible(false);
                        txt_Text_Quantity.setVisible(false);
                        btn_Order.setVisible(false);
                        pannel_listhistory.setVisible(false);
                        btn_add_product.setVisible(true);
                        btn_reduce_product.setVisible(true);
                        btn_Logout_2.setVisible(true);
                        combobox_Method.setVisible(false);
                        btn_admin_edit.setVisible(true);
                        txt_Text_Quantity.setVisible(false);
                        txt_change_Comment.setVisible(false);
                        txt_Comment.setVisible(false);
                        FLAG_admin=true;
                        //asign Customer ID
                        Customer_ID=rs.getString("customer_id");

                        txt_pass_input.setText("");

                        //Insert data into JList
                        for(Product product : arr_Products) {
                            model.addElement(product.getProduct_id());
                        }
                        list_product.setModel(model);

                        //Insert History of Customer into JList

                        //Insert payment method into ComboBox
                        insertPayMentMethod();
                        JOptionPane.showMessageDialog(pannel_main, "Login Succeed!!!");
                        pannel_login.setVisible(false);
                    }
                    else {
                       JOptionPane.showMessageDialog(pannel_main, "Email or Password was ivalid");
                       txt_pass_input.setText("");
                       txt_pass_input.setText("");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }); //Done

        //Button Add To Favorite
        btn_addtofavorite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var index=list_product.getSelectedIndex();
                var temp_product_id=arr_Products.get(index).getProduct_id();
                PreparedStatement stmt=null;
                var mysql="Call add_favorite(?, ?)";
                try {
                    stmt=(PreparedStatement) conn.prepareStatement(mysql);
                    stmt.setString(1, temp_product_id);
                    stmt.setString(2, Customer_ID);
                    int rs=stmt.executeUpdate();
                    if(rs!=-1) {
                        JOptionPane.showMessageDialog(pannel_main, "Succeed");
                    }
                    else {
                        JOptionPane.showMessageDialog(pannel_main, "Error");
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }); //Done

        //Button Edit
        btn_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreparedStatement stmt= null;
                ResultSet rs = null;
                var temp_email_confirm=txt_email_input.getText();
                try {
                    stmt = (PreparedStatement) conn.prepareStatement("Select * from Customer where email_login=?");
                    stmt.setString(1, temp_email_confirm);
                    rs=stmt.executeQuery();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                if(FLAG_edit==true) {
                    FLAG_edit=false;
                    txt_password_change_login.setText("Input your new password");
                }
                else {
                    FLAG_edit=true;
                    try {
                        if(!rs.next()) {
                            JOptionPane.showMessageDialog(pannel_main, "Invalid Email");
                        }
                        else {
                            var temp_newpassword=txt_pass_input.getText().toString();
                            if(temp_newpassword.isEmpty()) {
                                JOptionPane.showMessageDialog(pannel_main, "Please input new password!!!");
                            }
                            else {
                                PreparedStatement stmt_change_pass = (PreparedStatement) conn.prepareStatement("update Customer set password_login=? where email_login=?");
                                stmt_change_pass.setString(1, temp_newpassword);
                                stmt_change_pass.setString(2, temp_email_confirm);
                                boolean rs_change_password = stmt_change_pass.execute();
                                JOptionPane.showMessageDialog(pannel_main, "Change Password Succeed!!!");
                                btnLogin.setVisible(true);
                                btn_signup.setVisible(true);
                                txt_pass_input.setText("");
                                txt_password_change_login.setText("Password");
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }


            }
        });

        //Event when we search
        txt_find.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                //Create temp model for inserting mnew data into JList Product
                DefaultListModel<String> temp_product_model=new DefaultListModel<>();
                var temp_find_char=txt_find.getText();
                ArrayList<Product> arr_temp_product=new ArrayList<>();;
                Product temp_product=null;
                for(Product p : arr_Products) {
                    var result=p.getProduct_id().contains(temp_find_char);
                    if(result==true) {
                        temp_product=new Product();
                        temp_product.setProduct_id(p.getProduct_id());
                        temp_product.setProduct_price(p.getProduct_price());
                        temp_product.setProduct_type_code(p.getProduct_type_code());
                        arr_temp_product.add(temp_product);
                    }
                }
                for(Product c: arr_temp_product) {
                    temp_product_model.addElement(c.getProduct_id());
                }
                list_product.setModel(temp_product_model);
            }
        }); //Done

        //Button Logout Event
        btn_Logout_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt_email_input.setText("");
                txt_pass_input.setText("");
                txt_Quantity.setText("");
                txt_Comment.setText("");
                txt_text_change_dis_rename.setText("Discount");
                btnLogin.setVisible(true);
                btn_signup.setVisible(true);
                btn_edit.setVisible(true);
                btn_Order.setVisible(false);
                model_list_history.clear();
                comboBox_sort.removeAllItems();
                combobox_Method.removeAllItems();
                model.clear();
                list_History.setVisible(false);
                list_product.setVisible(false);
                pannel_login.setVisible(true);
                btn_add_product.setVisible(false);
                btn_reduce_product.setVisible(false);
                btn_Logout_2.setVisible(true);
                pannel_listhistory.setVisible(true);
                combobox_Method.setVisible(true);
                btn_addtofavorite.setVisible(false);
                FLAG_admin=false;
                JOptionPane.showMessageDialog(pannel_main, "Logout Succeed!!!");
            }
        }); //Done

        //Button Login Event
        btnLogin.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String temp_email=txt_email_input.getText();
                String temp_pass=txt_pass_input.getText().toString();
                connectData();
                try {

                    //Test email and password for logging
                    PreparedStatement stmt= (PreparedStatement) conn.prepareStatement("Call login(?, ?)");
                    stmt.setString(1, temp_email);
                    stmt.setString(2, temp_pass);
                    ResultSet rs=stmt.executeQuery();
                    if(rs.next()) {
                        btn_edit.setVisible(true);
                        btn_Logout_2.setVisible(true);
                        list_product.setVisible(true);
                        list_History.setVisible(true);
                        btn_Order.setVisible(true);
                        txt_change_Comment.setVisible(true);
                        txt_Comment.setVisible(true);
                        btn_addtofavorite.setVisible(true);
                        System.out.println("Done");

                        //asign Customer ID
                        Customer_ID=rs.getString("customer_id");

                        txt_pass_input.setText("");

                        CUSTOMER_POINT=rs.getInt("point");

                        //Insert data into JList
                        for(Product product : arr_Products) {
                            model.addElement(product.getProduct_id());
                        }
                        list_product.setModel(model);

                        //Insert History of Customer into JList
                        insertCustomerHistory();

                        //Insert payment method into ComboBox
                        insertPayMentMethod();
                        insertSort();
                        JOptionPane.showMessageDialog(pannel_main, "Login Succeed!!!");
                        pannel_login.setVisible(false);
                    }
                    else {
                        JOptionPane.showMessageDialog(pannel_main, "Invalid Username or Password");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }); //Done

        //Event for ComboBox Sort Type
        class ItemChangeListener implements ItemListener{
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Object item = event.getItem();
                    Sort_Type_Code=comboBox_sort.getSelectedIndex();
                    if(Sort_Type_Code==2) {
                        FLAG_sort=false;
                    }
                    if(FLAG_sort==false) {
                        btn_add_product.setVisible(false);
                    }
                    model.clear();
                    for(Product p : function.Sort(Sort_Type_Code, arr_Products, Customer_ID, conn, model, model_temp)) {
                        model.addElement(p.getProduct_id());
                    }
                    list_product.setModel(model);
                    FLAG=true;
                }
            }
        }

        comboBox_sort.addItemListener(new ItemChangeListener()); //Done

        //Button Sign Up event
        btn_signup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (FLAG == true) {
                    FLAG = false;
                    txt_email_input.setText("");
                    txt_pass_input.setText("");
                    txt_change_email_login.setText("Input your email: ");
                    txt_password_change_login.setText("Input your password");
                } else {
                    if (FLAG == false) {
                        FLAG = true;
                        var temp_customer_id = generateMyBigNumber(20);
                        var temp_new_email = txt_email_input.getText();
                        var temp_password = txt_pass_input.getText();
                        try {
                            PreparedStatement mysql_order = (PreparedStatement) conn.prepareStatement("Call sign_up(?, ?, ?)");
                            mysql_order.setString(1, temp_customer_id.toString());
                            mysql_order.setString(2, temp_new_email);
                            mysql_order.setString(3, temp_password.toString());
                            boolean rs = mysql_order.execute();
                            txt_change_email_login.setText("Email: ");
                            txt_password_change_login.setText("Password");
                            btnLogin.setVisible(true);
                            btn_edit.setVisible(true);
                            btn_admin.setVisible(true);
                            txt_pass_input.setText("");
                            txt_email_input.setText("");
                            JOptionPane.showMessageDialog(pannel_main, "Sign Up Succeed!!!");
                            txt_email_input.setText("");
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }

                }
            }
        }); //Done

        //Button Order Event
        btn_Order.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Event for Order
                if(FLAG_order==true) {
                    function.maximum_point(MAX_POINT, CUSTOMER_POINT, conn, pannel_main);
                    var eval=txt_Quantity.getText().toString(); //Get Amount of product which customer wanna order

                    //Set default quantity value if it was empty
                    if(eval.isEmpty()) {
                        eval="1";
                    }

                    var dis_code_temp=txt_discount_code.getText();
                    double temp_product_price;
                    int discount_value;
                    int base_product_price= Integer.parseInt(txt_product_price.getText());
                    PreparedStatement stmt_dis= null;
                    try {
                        stmt_dis = (PreparedStatement) conn.prepareStatement("Select * from Discount_Code where code=?");
                        stmt_dis.setString(1, dis_code_temp);
                        ResultSet rs_discount=stmt_dis.executeQuery();

                        if(rs_discount.next()) {
                            var discount_type_code=rs_discount.getInt("discount_type");
                            if(discount_type_code==1) {
                                discount_value=rs_discount.getInt("discount_value");
                                txt_product_price.setText(""+(base_product_price-discount_value));
                            }
                            else {
                                discount_value=rs_discount.getInt("percent_discount");
                                txt_product_price.setText(""+(base_product_price*(1.0*discount_value/100)));
                            }
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    btn_Order.setText("Order");
                    int temp_quantity;
                    temp_quantity=Integer.parseInt(eval);
                    int temp_method_code=combobox_Method.getSelectedIndex();
                    try {
                        //Create Order_ID
                        BigInteger temp_order_id=generateMyBigNumber(18);

                        //Get Payment Method ID
                        var temp_payment_id=arr_method.get(combobox_Method.getSelectedIndex()).getCustomer_payment_method_id();

                        //insert into table Custumer_order
                        PreparedStatement mysql_Customer_oder= (PreparedStatement) conn.prepareStatement("Call insert_customer_orderid(?, ?, ?, ?)");
                        mysql_Customer_oder.setString(1, temp_order_id.toString());
                        mysql_Customer_oder.setString(2, Customer_ID);
                        mysql_Customer_oder.setString(3, temp_payment_id);
                        mysql_Customer_oder.setString(4, String.valueOf(date));
                        var rs_customer_order=mysql_Customer_oder.executeUpdate();

                        //insert into table customer order product
                        PreparedStatement mysql_order= (PreparedStatement) conn.prepareStatement("call insert_customer_order_products(?, ?, ?, ?)");
                        mysql_order.setString(1, temp_order_id.toString());
                        mysql_order.setString(2, arr_Products.get(list_product.getSelectedIndex()).getProduct_id());
                        mysql_order.setInt(3, temp_quantity);
                        mysql_order.setString(4, txt_Comment.getText());
                        boolean rs=mysql_order.execute();

                        PreparedStatement stmt_count_point=(PreparedStatement) conn.prepareStatement("Call get_point(?)");
                        stmt_count_point.setString(1, Customer_ID);
                        var rs_get_point=stmt_count_point.executeQuery();
                        if(rs_get_point.next()) {
                            if(rs_get_point.getInt("point")==MAX_POINT) {
                                JOptionPane.showMessageDialog(pannel_main, "You recieve 1 discount code: BN01");
                                PreparedStatement stmt_point_reset=(PreparedStatement) conn.prepareStatement("Call update_point(?, ?)");
                                stmt_point_reset.setString(1, Customer_ID);
                                stmt_point_reset.setInt(2, 0);
                                var result_point_reset=stmt_point_reset.executeUpdate();
                                if(result_point_reset!=-1) {
                                    System.out.println("Done");
                                }
                            }
                        }

                        //increase point for Customer Ordering

                        insertCustomerHistory();
                        txt_Quantity.setText("");
                        txt_Comment.setText("");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }

                //Event for Re-order
                else {
                    function.maximum_point(MAX_POINT, CUSTOMER_POINT, conn, pannel_main);
                    var eval=txt_Quantity.getText();

                    if(eval.isEmpty()) {
                        eval="1";
                    }

                    var dis_code_temp=txt_discount_code.getText();
                    double temp_product_price;
                    int discount_value;
                    int base_product_price= Integer.parseInt(txt_product_price.getText());
                    PreparedStatement stmt_dis= null;
                    try {
                        stmt_dis = (PreparedStatement) conn.prepareStatement("Select * from Discount_Code where code=?");
                        stmt_dis.setString(1, dis_code_temp);
                        ResultSet rs_discount=stmt_dis.executeQuery();

                        if(rs_discount.next()) {
                            var discount_type_code=rs_discount.getInt("discount_type");
                            if(discount_type_code==1) {
                                discount_value=rs_discount.getInt("discount_value");
                                txt_product_price.setText(""+(base_product_price-discount_value));
                            }
                            else {
                                discount_value=rs_discount.getInt("percent_discount");
                                txt_product_price.setText(""+(base_product_price*(1.0*discount_value/100)));
                            }
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    btn_Order.setText("Re-Order");
                    FLAG_order=true;
                    int temp_quantity;
                        temp_quantity=Integer.parseInt(eval);

                    int temp_method_code=combobox_Method.getSelectedIndex();
                    try {

                        //Create Order_ID
                        BigInteger temp_order_id=generateMyBigNumber(18);

                        //Get Payment Method ID
                        var temp_payment_id=arr_method.get(combobox_Method.getSelectedIndex()).getCustomer_payment_method_id();

                        //insert into table Custumer_order
                        PreparedStatement mysql_Customer_oder= (PreparedStatement) conn.prepareStatement("Call insert_customer_orderid(?, ?, ?, ?)");
                        mysql_Customer_oder.setString(1, temp_order_id.toString());
                        mysql_Customer_oder.setString(2, Customer_ID);
                        mysql_Customer_oder.setString(3, temp_payment_id);
                        mysql_Customer_oder.setString(4, String.valueOf(date));
                        boolean rs_customer_order=mysql_Customer_oder.execute();

                        PreparedStatement stmt= (PreparedStatement) conn.prepareStatement("Select * from customer_order_products where order_id=?");
                        stmt.setString(1, ORDER_ID);
                        ResultSet rs_product_temp=stmt.executeQuery();
                        if(rs_product_temp.next()) {
                            var temp_product= rs_product_temp.getString("product_id");
                            PreparedStatement mysql_order= (PreparedStatement) conn.prepareStatement("Call insert_customer_order_products(?, ?, ?, ?)");
                            mysql_order.setString(1, temp_order_id.toString());
                            mysql_order.setString(2, temp_product);
                            mysql_order.setInt(3, temp_quantity);
                            mysql_order.setString(4, txt_Comment.getText());
                            boolean rs=mysql_order.execute();
                        }

                        PreparedStatement stmt_count_point=(PreparedStatement) conn.prepareStatement("Call get_point(?)");
                        stmt_count_point.setString(1, Customer_ID);
                        var rs_get_point=stmt_count_point.executeQuery();
                        if(rs_get_point.next()) {
                            if(rs_get_point.getInt("point")==MAX_POINT) {
                                JOptionPane.showMessageDialog(pannel_main, "You recieve 1 discount code: BN01");
                                PreparedStatement stmt_point_reset=(PreparedStatement) conn.prepareStatement("Call update_point(?)");
                                stmt_point_reset.setString(1, Customer_ID);
                                var result_point_reset=stmt_point_reset.executeUpdate();
                                if(result_point_reset!=-1) {
                                    System.out.println("Done");
                                }
                            }
                        }
                        //insert into table customer order product
                        list_History.clearSelection();
                        insertCustomerHistory();
                        txt_Quantity.setText("");
                        txt_Comment.setText("");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }); //Done

        //Event when select Jlist history
        list_History.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!model_list_history.isEmpty()) {
                    FLAG_order=false;
                    int temp_history_selection=list_History.getSelectedIndex();
                    if(temp_history_selection==-1) {
                        temp_history_selection=0;
                    }
                    var temp_order_id=arr_history.get(temp_history_selection).getOrder_id();
                    System.out.println(temp_history_selection);
                    ORDER_ID=temp_order_id;
                    btn_Order.setText("Re-Order");

                    PreparedStatement stmt= null;
                    try {
                        stmt = (PreparedStatement) conn.prepareStatement("Select * from customer_order_products where order_id=?");
                        stmt.setString(1, temp_order_id);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        ResultSet rs=stmt.executeQuery();
                        if(rs.next()) {
                            txt_Comment.setText(rs.getString("comment"));
                            txt_Quantity.setText(String.valueOf(rs.getInt("quantity")));
                            PreparedStatement stmt_2= (PreparedStatement) conn.prepareStatement("Select * from product where product_id=?");
                            stmt_2.setString(1, rs.getString("product_id"));
                            ResultSet rs_2=stmt_2.executeQuery();
                            if(rs_2.next()) {
                                txt_product_price.setText(String.valueOf(rs_2.getInt("product_price")));
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }); //Done

        list_product.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!model.isEmpty()) {
                    txt_Comment.setText("");
                    int temp_order_id=list_product.getSelectedIndex();
                    if(temp_order_id==-1) {
                        temp_order_id=0;
                    }
                    System.out.println(temp_order_id);
                    btn_Order.setText("Order");
                    if(FLAG_admin==true) {
                        txt_discount_code.setText(arr_Products.get(temp_order_id).getProduct_id());
                    }
                    var price=arr_Products.get(temp_order_id).getProduct_price();
                    txt_product_price.setText(""+price);
                }
            }
        });

    }


    //Insert data into JList History
    private void insertCustomerHistory() throws SQLException {
        model_list_history.clear();
        arr_history=new ArrayList<>();
        PreparedStatement stmt= null;
        try {
            stmt = (PreparedStatement) conn.prepareStatement("Select * from Custumer_order where customer_id=?");
            stmt.setString(1, Customer_ID);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ResultSet rs=stmt.executeQuery();
        while (rs.next()) {
            customer_history=new Custumer_order();
            customer_history.setOrder_id(rs.getString("order_id"));
            customer_history.setCustomer_id(rs.getString("customer_id"));
            customer_history.setCustomer_payment_method_id(rs.getString("customer_payment_method_id"));
            customer_history.setDate_order_place(String.valueOf(rs.getDate("date_order_place")));
            arr_history.add(customer_history);
        }

        for(Custumer_order c : arr_history) {
            PreparedStatement stmt_2= null;
            try {
                stmt_2 = (PreparedStatement) conn.prepareStatement("Select * from customer_order_products where order_id=?");
                stmt_2.setString(1, c.getOrder_id());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            ResultSet rs_2=stmt_2.executeQuery();
            while (rs_2.next()) {
                model_list_history.addElement(rs_2.getString("product_id"));
            }
        }
        list_History.setModel(model_list_history);
    } //Done

    //Insert data into JComboBox Sort
    private void insertSort(){
        arr_sort_code.clear();
        arr_sort_code.add("Price: Increase");
        arr_sort_code.add("Price: Decrease");
        arr_sort_code.add("Favorite");
        arr_sort_code.add("New");
        for(String s : arr_sort_code) {
            comboBox_sort.addItem(s);
        }

    } //Done

    //Insert Method into JcomboBox Payment Method
    private void insertPayMentMethod() throws SQLException {
        PreparedStatement stmt= null;
        try {
            stmt = (PreparedStatement) conn.prepareStatement("Select * from customer_payment_method");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ResultSet rs=stmt.executeQuery();
        while (rs.next()) {
            method=new Customer_payment_method();
            method.setCustomer_payment_method_id(rs.getString("customer_payment_method_id"));
            method.setPayment_method_name(rs.getString("payment_method_name"));
            arr_method.add(method);
        }
        for(int i=0;i<3;i++) {
            combobox_Method.addItem(arr_method.get(i).getPayment_method_name());
        }
    } //Done

    //Random number
    public static BigInteger generateMyBigNumber(int bit){
        Random ran = new Random();
        BigInteger res = new BigInteger(bit, ran);
        return res;
    } //Done


    //Connect to Mysql Database
    private static void connectData(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //Download JDBC
            String url = "jdbc:mysql://localhost:3306/project";// your db name
            String user = "root"; // your db username
            String password = "hao152903"; // your db password
            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("Connect success!");
            }
            var sql_1 = "select * from Customer";
            var sql_2 = "select * from product";
            var sql_3="select * from Discount_Code";
            var statement = conn.prepareStatement(sql_1);
            var resultSet = statement.executeQuery();

            var statement_2=conn.prepareStatement(sql_2);
            var resultSet_2 = statement_2.executeQuery();

            arr_Products=new ArrayList<>();
            arr_history=new ArrayList<>();


            while (resultSet.next()) {
                mysql=new User();
                mysql.setId(resultSet.getString("customer_id"));
                mysql.setEmail(resultSet.getString("email_login"));
                mysql.setPassword(resultSet.getString("password_login"));
                arr_Mysql.add(mysql);
            }

            while(resultSet_2.next()) {
                product=new Product();
                product.setProduct_id(resultSet_2.getString("product_id"));
                product.setProduct_type_code(resultSet_2.getString("product_type_code"));
                product.setProduct_price(resultSet_2.getInt("product_price"));
                arr_Products.add(product);
            }


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main (String args[]){
        Screen screen = new Screen();
        screen.setContentPane(new Screen().pannel_main);
        screen.pack();
        screen.setVisible(true);
        connectData();
    }
}




