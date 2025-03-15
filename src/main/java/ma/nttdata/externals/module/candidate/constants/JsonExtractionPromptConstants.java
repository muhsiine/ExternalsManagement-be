package ma.nttdata.externals.module.candidate.constants;

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
                  "type": "string"
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

}
