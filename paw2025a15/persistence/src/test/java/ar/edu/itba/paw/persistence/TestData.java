package ar.edu.itba.paw.persistence;

import java.time.LocalDate;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.FileTypeEnum;
import ar.edu.itba.paw.models.LocaleEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;

//Readable output of all the data inputed in test/resources
public class TestData {

    public class Images{
        public static final File validImage = new File(1L, "D".getBytes(), FileTypeEnum.JPEG);
        public static final File validImage2 = new File(2L, "O".getBytes(), FileTypeEnum.JPEG);
    }

    public class Users{
        public static final User patient = new User(1, "donna@example.com", "supersecret123", "donna", "1144445555", UserRoleEnum.PATIENT, 1, LocalDate.parse("2025-04-09") , LocaleEnum.EN_US);
        public static final User newPatient = new User(2, "sophie@example.com", "supersecret123", "sophie", "1144445555", UserRoleEnum.PATIENT, 1, LocalDate.now() , LocaleEnum.EN_US);
    }
}
