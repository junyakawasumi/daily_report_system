package controllers.employees;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;

/**
 * Servlet implementation class EmployeesIndexServlet
 */
@WebServlet("/employees/index")
public class EmployeesIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        //pagination
        //開くページ数を取得（デフォルトは1ページ）
        int page = 1;
        try{
            page = Integer.parseInt(request.getParameter("page"));
        } catch(NumberFormatException e) { }

        List<Employee> employees = em.createNamedQuery("getAllEmployees", Employee.class)
                                     .setFirstResult(15 * (page - 1)) //何件目からデータを取得するか
                                     .setMaxResults(15) //データの最大取得件数
                                     .getResultList(); //問合せ結果をリストで取得

        long employees_count = (long)em.createNamedQuery("getEmployeesCount", Long.class)
                                       .getSingleResult(); //全データの件数を取得

        em.close();

        request.setAttribute("employees", employees); //取得したMAX15件のデータをリクエストスコープに保存
        request.setAttribute("employees_count", employees_count); //全データの件数をリクエストスコープに保存
        request.setAttribute("page", page); //ページ数をリクエストスコープに保存

        //フラッシュメッセージがセッションスコープに格納されていたら取得し、リクエストスコープに格納(セッションスコープからは削除)
        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        //フォワード
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/index.jsp");
        rd.forward(request, response);
    }

}
