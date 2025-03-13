package ma.nttdata.externals.module.candidate.constants;

public final class JsonSchema {
    private JsonSchema() {}
//    public static final String jsonSchema = """
//        {
//          "type": "object",
//          "properties": {
//            "fullName": {"type": "string"},
//            "birthdate": {"type": "string"},
//            "mainTech": {"type": "string"},
//            "createdAt": {"type": "string"},
//            "contacts": {
//              "type": "array",
//              "items": {
//                "type": "object",
//                "properties": {
//                  "email": {"type": "string"},
//                  "phone": {"type": "string"}
//                }
//              }
//            },
//            "experiences": {
//              "type": "array",
//              "items": {
//                "type": "object",
//                "properties": {
//                  "companyName": {"type": "string"},
//                  "position": {"type": "string"},
//                  "startDate": {"type": "string"},
//                  "endDate": {"type": "string"},
//                  "description": {"type": "string"}
//                }
//              }
//            },
//            "skills": {
//              "type": "array",
//              "items": {
//                "type": "object",
//                "properties": {
//                  "skillName": {"type": "string"},
//                  "proficiencyLevel": {"type": "string"}
//                }
//              }
//            },
//            "educations": {
//              "type": "array",
//              "items": {
//                "type": "object",
//                "properties": {
//                  "institution": {"type": "string"},
//                  "degree": {"type": "string"},
//                  "startDate": {"type": "string"},
//                  "endDate": {"type": "string"}
//                }
//              }
//            },
//            "cvFiles": {
//              "type": "array",
//              "items": {
//                "type": "object",
//                "properties": {
//                  "fileName": {"type": "string"},
//                  "mimeType": {"type": "string"},
//                  "fileContent": {"type": "string"}
//                }
//              }
//            }
//          }
//        }
//        """;
    public static final String jsonSchema = """
        {
          "type": "object",
          "properties": {
            "candidate": {
              "type": "object",
              "properties": {
                "fullName": {
                  "type": "string"
                },
                "birthDate": {
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
                        "type": "string",
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
                    }
                  },
                  "required": [
                    "country",
                    "city"
                  ]
                },
                "naturalLanguage": {
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
                      "native": {
                        "type": "boolean"
                      },
                      "languageInEnglish": {
                        "type": "string"
                      }
                    },
                    "required": [
                      "language",
                      "level",
                      "fullDescription",
                      "native",
                      "languageInEnglish",
                      "englishDescription"
                    ]
                  }
                }
              },
              "required": [
                "fullName",
                "birthDate",
                "contacts",
                "skills",
                "address",
                "naturalLanguage"
              ]
            }
          }
        }
        """;
    public static final String json = """
            {
              "fullName": "string",
              "birthdate": "string",
              "mainTech": "string",
              "summary": "string",
              "createdAt": "string",
              "contacts": [
                {
                  "email": "string",
                  "phone": "string"
                }
              ],
              "experiences": [
                {
                  "companyName": "string",
                  "position": "string",
                  "startDate": "string",
                  "endDate": "string",
                  "description": "string"
                }
              ],
              "skills": [
                {
                  "skillName": "string",
                  "proficiencyLevel": "string"
                },
                {
                  "skillName": "string",
                  "proficiencyLevel": "string"
                }
              ],
              "educations": [
                {
                  "institution": "string",
                  "degree": "string",
                  "startDate": "string",
                  "endDate": "string"
                }
              ],
              "cvFiles": [
                {
                  "fileName": "string",
                  "mimeType": "string",
                  "fileContent": "string"
                }
              ]
            }
    """;
}
