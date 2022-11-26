import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "statistics")
public class Statistics {
    @JacksonXmlProperty(localName = "violation")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<Violation> violations;

    public Statistics(List<Violation> violations) {
        this.violations = violations;
    }

}
