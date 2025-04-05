package ar.edu.itba.paw.webapp.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.models.Doctor;


@Controller
public class HelloWorldController {

    private List<Doctor> getDocsNames(){
        List<Doctor> docsName = List.of(
                new Doctor(1, "Santi SillaNegra", "Santi@gmail.com", List.of("Osde","Medicus"), "traumatologo", null),
                new Doctor(1, "Manu Santamarina", "Manu@gmail.com", List.of("Osde","Medicus"), "Otorrino", null),
                new Doctor(1, "Pepe pepon", "Pepe@gmail.com", List.of("Osde","Medicus"), "cirujano", null)
        );
        return docsName;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("docList", getDocsNames());
        return mav;
    }

    @RequestMapping("/estudios")
    public ModelAndView estudios() {
        final ModelAndView mav = new ModelAndView("estudios");
        return mav;
    }

    @RequestMapping("/obras-sociales")
    public ModelAndView obrasSociales() {
        final ModelAndView mav = new ModelAndView("obras-sociales");
        return mav;
    }

}