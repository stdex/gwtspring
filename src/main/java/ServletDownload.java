import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.userBase.shared.dto.UserDTO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ServletDownload extends RemoteServiceServlet {


    @Override
    protected void doGet(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition",
                "attachment;filename=UserBase.xml");

        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        EntityManagerFactory emfInstance = (EntityManagerFactory)context.getBean("entityManagerFactory");

        EntityManager em = emfInstance.createEntityManager();
        Query q = em.createQuery("select u from UserDTO u");
        List<UserDTO> result = q.getResultList();

        OutputStream os = response.getOutputStream();
        os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r".getBytes());
        os.write("<table name='UserBase'>\r".getBytes());

        for (int i = 0; i < result.size(); i++) {
            os.write("  <tr>\r".getBytes());
            os.write("    <td name='ID'>".getBytes());
            os.write((String.valueOf(result.get(i).getUserId())).getBytes());
            os.write("</td>\r".getBytes());
            os.write("    <td name='Name'>".getBytes());
            os.write(result.get(i).getUserName().getBytes());
            os.write("</td>\r".getBytes());
            os.write("    <td name='PhoneNumber'>".getBytes());
            os.write(result.get(i).getPhoneNumber().getBytes());
            os.write("</td>\r".getBytes());
            os.write("  </tr>\r".getBytes());
        }

        os.write("</table>\r".getBytes());

        os.flush();
        os.close();

    }
}
