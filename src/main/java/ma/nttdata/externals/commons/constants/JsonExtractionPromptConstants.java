package ma.nttdata.externals.commons.constants;

public final class JsonExtractionPromptConstants {
    private JsonExtractionPromptConstants() {}
    public static final String text = """
    I'll give you this cv/resume, please give me the candidate data, please try to put the maximum amount of data in the description fields,
    please take into account skill and natural languages rankings/levels could be  represented by either a star-based system or a progress bar, where the number of stars or the filled percentage of the progress bar indicates the proficiency level. The representation might be visually styled differently,
    but the number of stars or the progress bar's filled portion remains the key to determining the level.
    It could be more complex than that, then you should interpret it visually the part around it to deduce the level.
    Try your best to deduce levels by any means visually contextually..
    """;

    public static final String jsonSchema = """
        {
          "type": "object",
          "properties": {
            "fullName": {
              "type": "string"
            },
            "birthDate": {
              "type": "string", "description": "the date should be in the form of YYYY-MM-DD"
            },
            "yearsOfExperience": {
              "type": "number"
            },
            "gender": {
              "type": "string",
              "enum": [
                  "F",
                  "M"
              ]
            },
            "summary":{
                "type": "string"
            },
            "mainTech": {
              "type": "string", "description":"try to deduce the mainTech from the whole CV"
            },
            "contacts": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "contactType": {
                    "type": "string"
                  },
                  "contactValue": {
                    "type": "string"
                  }
                },
                "required": [
                  "contactType",
                  "contactValue"
                ]
              }
            },
            "experiences": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "companyName": {
                    "type": "string"
                  },
                  "position": {
                    "type": "string"
                  },
                  "startDate": {
                    "type": "string", "description": "the date should be in the form of YYYY-MM-DD"
                  },
                  "endDate": {
                    "type": "string", "description": "the date should be in the form of YYYY-MM-DD"
                  },
                  "description": {
                    "type": "string"
                  }
                }
              }
            },
            "skills": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "skillName": {
                    "type": "string"
                  },
                  "proficiencyLevel": {
                    "type": "string", "description": "default to BEGINNER",
                    "enum": [
                      "BEGINNER",
                      "INTERMEDIATE",
                      "EXPERT"
                    ]
                  }
                },
                "required": [
                  "skillName",
                  "proficiencyLevel"
                ]
              }
            },
            "educations": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "institution": {
                    "type": "string"
                  },
                  "diploma": {
                    "type": "string"
                  },
                  "startDate": {
                    "type": "string", "description": "the date should be in the form of YYYY-MM-DD"
                  },
                  "endDate": {
                    "type": "string", "description": "the date should be in the form of YYYY-MM-DD"
                  }
                }
              }
            },
            "address": {
              "type": "object",
              "properties": {
                "country": {
                  "type": "object",
                  "properties": {
                    "name": {
                      "type": "string"
                    },
                    "englishName": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "name",
                    "englishName"
                  ]
                },
                "city": {
                  "type": "object",
                  "properties": {
                    "name": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "name"
                  ]
                },
                "postalCode": {
                  "type": "string", "description": "try you best to get it from the whole CV"
                },
                "street": {
                  "type": "string"
                },
                "fullAddress": {
                    "type": "string"
                }
              },
              "required": [
                "country",
                "city",
                "street"
              ]
            },
            "naturalLanguages": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "language": {
                    "type": "string"
                  },
                  "level": {
                    "type": "string",
                    "enum": [
                      "BEGINNER",
                      "LOWER_INTERMEDIATE",
                      "INTERMEDIATE",
                      "UPPER_INTERMEDIATE",
                      "ADVANCED"
                    ],
                    "comment": "focus only in the part around the language, don't consider the whole file, also consider (Between A1 and A2) or [*    ] -> BEGINNER, (Between A2 and B1) or [**   ]-> LOWER_INTERMEDIATE, (Between B1 and B2) or [***  ] -> INTERMEDIATE, (B2 and C1) or [**** ] -> UPPER_INTERMEDIATE, (More than C1 or native) or [*****] -> ADVANCED"
                  },
                  "englishDescription": {
                    "type": "string"
                  },
                  "fullDescription": {
                    "type": "string"
                  },
                  "description": {
                    "type": "string"
                  },
                  "isNative": {
                    "type": "boolean"
                  },
                  "languageInEnglish": {
                    "type": "string"
                  }
                },
                "required": [
                  "language",
                  "level",
                  "englishDescription",
                  "fullDescription",
                  "description",
                  "isNative",
                  "languageInEnglish"
                ]
              }
            }
          },
          "required": [
            "fullName",
            "gender",
            "contacts",
            "skills",
            "address",
            "naturalLanguages"
          ]
        }
        """;
    public static final String jsonMock = """
            {
              "fullName": "Jane Doe",
              "birthDate": "1990-05-15",
              "yearsOfExperience": 8,
              "gender": "F",
              "summary": "Experienced software engineer with a focus on web development and a passion for creating efficient and user-friendly applications.",
              "mainTech": "JavaScript",
              "contacts": [
                {
                  "contactType": "email",
                  "contactValue": "jane.doe@example.com"
                },
                {
                  "contactType": "phone",
                  "contactValue": "+15551234567"
                },
                {
                  "contactType": "linkedin",
                  "contactValue": "linkedin.com/in/janedoe"
                }
              ],
              "experiences": [
                {
                  "companyName": "TechCorp",
                  "position": "Senior Software Engineer",
                  "startDate": "2018-01-01",
                  "endDate": "2023-12-31",
                  "description": "Developed and maintained web applications using React and Node.js. Led a team of junior developers."
                },
                {
                  "companyName": "WebSolutions",
                  "position": "Software Engineer",
                  "startDate": "2016-01-01",
                  "endDate": "2017-12-31",
                  "description": "Worked on front-end development using Angular and contributed to back-end development with Python."
                }
              ],
              "skills": [
                {
                  "skillName": "JavaScript",
                  "proficiencyLevel": "EXPERT"
                },
                {
                  "skillName": "React",
                  "proficiencyLevel": "EXPERT"
                },
                {
                  "skillName": "Node.js",
                  "proficiencyLevel": "INTERMEDIATE"
                },
                {
                  "skillName": "Python",
                  "proficiencyLevel": "INTERMEDIATE"
                },
                {
                  "skillName": "Angular",
                  "proficiencyLevel": "INTERMEDIATE"
                }
              ],
              "educations": [
                {
                  "institution": "University of California, Berkeley",
                  "diploma": "Bachelor of Science in Computer Science",
                  "startDate": "2012-09-01",
                  "endDate": "2016-05-31"
                }
              ],
              "address": {
                "country": {
                  "name": "United States",
                  "englishName": "United States"
                },
                "city": {
                  "name": "San Francisco"
                },
                "postalCode": "94101",
                "street": "123 Main St",
                "fullAddress": "123 Main St, San Francisco, CA 94101, United States"
              },
              "naturalLanguages": [
                {
                  "language": "English",
                  "level": "ADVANCED",
                  "englishDescription": "Native speaker",
                  "fullDescription": "Native speaker of English.",
                  "description": "Native",
                  "isNative": true,
                  "languageInEnglish": "English"
                },
                {
                    "language": "Spanish",
                    "level": "INTERMEDIATE",
                    "englishDescription": "Intermediate proficiency",
                    "fullDescription": "Has an intermediate level of Spanish.",
                    "description": "Intermediate",
                    "isNative": false,
                    "languageInEnglish": "Spanish"
                }
              ]
            }
            """;

}
