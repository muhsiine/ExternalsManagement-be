package ma.nttdata.externals.commons.data.mock;

import ma.nttdata.externals.module.candidate.constants.GenderEnum;
import ma.nttdata.externals.module.candidate.constants.LanguageLevel;
import ma.nttdata.externals.module.candidate.constants.ProficiencyLevel;
import ma.nttdata.externals.module.candidate.entity.*;
import ma.nttdata.externals.module.candidate.repository.CandidateRepository;
import ma.nttdata.externals.module.candidate.repository.CityRepository;
import ma.nttdata.externals.module.candidate.repository.CountryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("local")
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final CandidateRepository candidateRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final Random random = new Random();

    public DataInitializer(CandidateRepository candidateRepository, CityRepository cityRepository, CountryRepository countryRepository) {
        this.candidateRepository = candidateRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public void run(String... args) {

        Country morocco = createCountry("Maroc", "Morocco");

        List<City> moroccanCities = createCities(morocco);

        int numberOfCandidates = 100;

        for (int i = 0; i < numberOfCandidates; i++) {
            City city = getRandomCity(moroccanCities);
            Candidate candidate = createRandomCandidate(city, morocco);
            candidateRepository.save(candidate);
        }
    }

    private Candidate createRandomCandidate(City city, Country country) {
        Candidate candidate = new Candidate();
        candidate.setFullName(generateRandomFullName());
        candidate.setBirthDate(generateRandomBirthDate());
        candidate.setYearsOfExperience(random.nextInt(20));
        candidate.setGender(random.nextBoolean() ? GenderEnum.M : GenderEnum.F);
        candidate.setMainTech(generateRandomTech());
        candidate.setSummary(generateRandomSummary());

        Address address = new Address();
        address.setStreet(generateRandomStreet());
        address.setPostalCode(String.valueOf(random.nextInt(90000) + 10000));
        address.setFullAddress(address.getStreet() + ", " + city.getName());
        address.setCity(city);
        address.setCountry(country);
        address.setCandidate(candidate);
        candidate.setAddress(address);

        candidate.setContacts(generateRandomContacts(candidate));
        candidate.setExperiences(generateRandomExperiences(candidate));
        candidate.setSkills(generateRandomSkills(candidate));
        candidate.setEducations(generateRandomEducations(candidate));
        candidate.setLanguages(generateRandomLanguages(candidate));

        return candidate;
    }

    // Helper methods for generating random data
    private String generateRandomFullName() {
        String[] firstNames = {"John", "Jane", "Alice", "Bob", "Charlie"};
        String[] lastNames = {"Doe", "Smith", "Johnson", "Williams", "Brown"};
        return firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
    }

    private LocalDate generateRandomBirthDate() {
        return LocalDate.now().minusYears(random.nextInt(50) + 20).minusDays(random.nextInt(365));
    }

    private String generateRandomTech() {
        String[] techs = {"Java", "Python", "JavaScript", "Spring Boot", "React"};
        return techs[random.nextInt(techs.length)];
    }

    private String generateRandomSummary() {
        String[] summaries = {"Experienced developer.", "Passionate about coding.", "Team player.", "Problem solver."};
        return summaries[random.nextInt(summaries.length)];
    }

    private String generateRandomStreet() {
        return random.nextInt(1000) + " " + "Random St";
    }

    private List<Contact> generateRandomContacts(Candidate candidate) {
        List<Contact> contacts = new ArrayList<>();
        Contact contact1 = new Contact();
        contact1.setContactType("Email");
        contact1.setContactValue("random" + random.nextInt(1000) + "@example.com");
        contact1.setCandidate(candidate);
        contacts.add(contact1);
        Contact contact2 = new Contact();
        contact2.setContactType("Phone");
        contact2.setContactValue("+1" + (random.nextInt(900000000) + 100000000));
        contact2.setCandidate(candidate);
        contacts.add(contact2);
        return contacts;
    }

    private List<Experience> generateRandomExperiences(Candidate candidate) {
        List<Experience> experiences = new ArrayList<>();
        Experience experience = new Experience();
        experience.setCompanyName("Random Company");
        experience.setPosition("Software Engineer");
        experience.setStartDate("2020-01-01");
        experience.setEndDate("2023-01-01");
        experience.setDescription("Developed random features.");
        experience.setCandidate(candidate);
        experiences.add(experience);
        return experiences;
    }

    private List<Skill> generateRandomSkills(Candidate candidate) {
        List<Skill> skills = new ArrayList<>();
        Skill skill = new Skill();
        skill.setSkillName(generateRandomTech());
        skill.setProficiencyLevel(ProficiencyLevel.values()[random.nextInt(ProficiencyLevel.values().length)]);
        skill.setCandidate(candidate);
        skills.add(skill);
        return skills;
    }

    private List<Education> generateRandomEducations(Candidate candidate) {
        List<Education> educations = new ArrayList<>();
        Education education = new Education();
        education.setInstitution("Random University");
        education.setDegree("Bachelor's");
        education.setStartDate("2016-01-01");
        education.setEndDate("2020-01-01");
        education.setDiploma("Bachelor Degree");
        education.setCandidate(candidate);
        educations.add(education);
        return educations;
    }

    private List<Language> generateRandomLanguages(Candidate candidate) {
        List<Language> languages = new ArrayList<>();
        Language language = new Language();
        language.setDescription("English");
        language.setEnglishDescription("English");
        language.setFullDescription("English Language");
        language.setLanguage("English");
        language.setLanguageInEnglish("English");
        language.setLevel(LanguageLevel.values()[random.nextInt(LanguageLevel.values().length)]);
        language.setNative(random.nextBoolean());
        language.setCandidate(candidate);
        languages.add(language);
        return languages;
    }

    private Country createCountry(String name, String englishName) {
        Country country = new Country();
        country.setName(name);
        country.setEnglishName(englishName);
        return countryRepository.save(country);
    }

    private List<City> createCities(Country country) {
        List<City> cities = new ArrayList<>();
        cities.add(createCity("Casablanca", country));
        cities.add(createCity("Rabat", country));
        cities.add(createCity("Marrakech", country));
        cities.add(createCity("Fes", country));
        cities.add(createCity("Tangier", country));
        cities.add(createCity("Agadir", country));
        cities.add(createCity("Tetouan", country));
        cities.add(createCity("Oujda", country));
        cities.add(createCity("Safi", country));
        cities.add(createCity("Kenitra", country));
        cities.add(createCity("Essaouira", country));
        cities.add(createCity("Nador", country));
        cities.add(createCity("Chefchaouen", country));
        return cities;
    }

    private City createCity(String name, Country country) {
        City city = new City();
        city.setName(name);
        city.setCountry(country);
        return cityRepository.save(city);
    }

    private City getRandomCity(List<City> cities) {
        return cities.get(random.nextInt(cities.size()));
    }
}