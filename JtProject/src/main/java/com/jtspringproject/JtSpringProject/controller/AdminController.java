package com.jtspringproject.JtSpringProject.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jtspringproject.JtSpringProject.models.Category;
import com.jtspringproject.JtSpringProject.models.Product;
import com.jtspringproject.JtSpringProject.models.User;
import com.jtspringproject.JtSpringProject.services.categoryService;
import com.jtspringproject.JtSpringProject.services.productService;
import com.jtspringproject.JtSpringProject.services.userService;

@Controller
public class AdminController {

    private final userService userService;
    private final categoryService categoryService;
    private final productService productService;

    @Autowired
    public AdminController(userService userService, categoryService categoryService, productService productService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.productService = productService;
    }
    
    // Root endpoint: redirect ke /login
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }
    
    // Endpoint register: menampilkan halaman register
    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register"); // akan mencari /views/register.jsp
    }
    
    // Endpoint login (tanpa prefix "/admin")
    @GetMapping("/login")
    public ModelAndView adminlogin(@RequestParam(required = false) String error) {
        ModelAndView mv = new ModelAndView("adminlogin"); // akan mencari /views/adminlogin.jsp
        if ("true".equals(error)) {
            mv.addObject("msg", "Invalid username or password. Please try again.");
        }
        return mv;
    }
    
    // Endpoint admin home (accessible melalui /admin atau /admin/Dashboard)
    @GetMapping({"/admin", "/admin/Dashboard"})
    public ModelAndView adminHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView mv = new ModelAndView("adminHome"); // akan mencari /views/adminHome.jsp
        mv.addObject("admin", authentication.getName());
        return mv;
    }
    
    // Endpoint admin index
    @GetMapping("/admin/index")
    public String index(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        return "index";  // akan mencari /views/index.jsp         
    }
    
    // Endpoint untuk kategori
    @GetMapping("/admin/categories")
    public ModelAndView getcategory() {
        ModelAndView mView = new ModelAndView("categories"); // /views/categories.jsp
        List<Category> categories = this.categoryService.getCategories();
        mView.addObject("categories", categories);
        return mView;
    }
    
    @PostMapping("/admin/categories")
    public String addCategory(@RequestParam("categoryname") String category_name) {
        Category category = this.categoryService.addCategory(category_name);
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/admin/categories/delete")
    public String removeCategoryDb(@RequestParam("id") int id) {    
        this.categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/admin/categories/update")
    public String updateCategory(@RequestParam("categoryid") int id, @RequestParam("categoryname") String categoryname) {
        this.categoryService.updateCategory(id, categoryname);
        return "redirect:/admin/categories";
    }
    
    // Endpoint untuk produk
    @GetMapping("/admin/products")
    public ModelAndView getproduct() {
        ModelAndView mView = new ModelAndView("products"); // /views/products.jsp
        List<Product> products = this.productService.getProducts();
        if (products.isEmpty()) {
            mView.addObject("msg", "No products are available");
        } else {
            mView.addObject("products", products);
        }
        return mView;
    }
    
    @GetMapping("/admin/products/add")
    public ModelAndView addProduct() {
        ModelAndView mView = new ModelAndView("productsAdd"); // /views/productsAdd.jsp
        List<Category> categories = this.categoryService.getCategories();
        mView.addObject("categories", categories);
        return mView;
    }
    
    @RequestMapping(value = "/admin/products/add", method = RequestMethod.POST)
    public String addProduct(@RequestParam("name") String name,
                             @RequestParam("categoryid") int categoryId,
                             @RequestParam("price") int price,
                             @RequestParam("weight") int weight,
                             @RequestParam("quantity") int quantity,
                             @RequestParam("description") String description,
                             @RequestParam("productImage") String productImage) {
        Category category = this.categoryService.getCategory(categoryId);
        Product product = new Product();
        product.setId(categoryId);
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setPrice(price);
        product.setImage(productImage);
        product.setWeight(weight);
        product.setQuantity(quantity);
        this.productService.addProduct(product);
        return "redirect:/admin/products";
    }
    
    @GetMapping("/admin/products/update/{id}")
    public ModelAndView updateproduct(@PathVariable("id") int id) {
        ModelAndView mView = new ModelAndView("productsUpdate"); // /views/productsUpdate.jsp
        Product product = this.productService.getProduct(id);
        List<Category> categories = this.categoryService.getCategories();
        mView.addObject("categories", categories);
        mView.addObject("product", product);
        return mView;
    }
    
    @RequestMapping(value = "/admin/products/update/{id}", method = RequestMethod.POST)
    public String updateProduct(@PathVariable("id") int id,
                                @RequestParam("name") String name,
                                @RequestParam("categoryid") int categoryId,
                                @RequestParam("price") int price,
                                @RequestParam("weight") int weight,
                                @RequestParam("quantity") int quantity,
                                @RequestParam("description") String description,
                                @RequestParam("productImage") String productImage) {
        // Implementasi update produk jika diperlukan
        return "redirect:/admin/products";
    }
    
    @GetMapping("/admin/products/delete")
    public String removeProduct(@RequestParam("id") int id) {
        this.productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
    
    @PostMapping("/admin/products")
    public String postproduct() {
        return "redirect:/admin/categories";
    }
    
    // Endpoint untuk menampilkan data pelanggan
    @GetMapping("/admin/customers")
    public ModelAndView getCustomerDetail() {
        ModelAndView mView = new ModelAndView("displayCustomers"); // /views/displayCustomers.jsp
        List<User> users = this.userService.getUsers();
        mView.addObject("customers", users);
        return mView;
    }
    
    // Endpoint untuk menampilkan profile pengguna
    @GetMapping("/admin/profileDisplay")
    public String profileDisplay(Model model) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava", "root", "");
            PreparedStatement stmt = con.prepareStatement("select * from users where username = ?;");
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            stmt.setString(1, username);
            ResultSet rst = stmt.executeQuery();
            if (rst.next()) {
                model.addAttribute("userid", rst.getInt(1));
                model.addAttribute("username", rst.getString(2));
                model.addAttribute("email", rst.getString(3));
                model.addAttribute("password", rst.getString(4));
                model.addAttribute("address", rst.getString(5));
            }
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        return "updateProfile"; // /views/updateProfile.jsp
    }
    
    // Endpoint untuk update profile pengguna
    @RequestMapping(value = "/admin/updateuser", method = RequestMethod.POST)
    public String updateUserProfile(@RequestParam("userid") int userid,
                                    @RequestParam("username") String username,
                                    @RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    @RequestParam("address") String address) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecommjava", "root", "");
            PreparedStatement pst = con.prepareStatement("update users set username= ?, email = ?, password= ?, address= ? where uid = ?;");
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, password);
            pst.setString(4, address);
            pst.setInt(5, userid);
            pst.executeUpdate();
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        return "redirect:/admin/index";
    }
}
