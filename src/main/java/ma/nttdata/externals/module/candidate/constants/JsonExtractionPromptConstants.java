package ma.nttdata.externals.module.candidate.constants;

public final class JsonExtractionPromptConstants {
    private JsonExtractionPromptConstants() {}
    public static final String text = "I'll give you this cv/resume, please give me the candidate data";

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
              "type": "string"
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
                    "type": "string"
                  },
                  "endDate": {
                    "type": "string"
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
                  "startDate": {
                    "type": "string"
                  },
                  "endDate": {
                    "type": "string"
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
                "postalCode",
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
                      "INTERMEDIATE",
                      "ADVANCED"
                    ],
                    "description": "if it's native, it should be Advanced"
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
            "birthDate",
            "gender",
            "contacts",
            "skills",
            "address",
            "naturalLanguages"
          ]
        }
        """;

}
