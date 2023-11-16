import { ApiProperty } from "@nestjs/swagger";
import {
  IsBoolean,
  IsString,
  IsNotEmpty,
  IsEmail,
  IsInt,
  MaxLength,
  IsOptional,
} from "class-validator";

export class UserInfoDto {
  @ApiProperty({
    example: "user@example.com",
    description: "The email of the user",
  })
  @IsEmail()
  @IsNotEmpty()
  @MaxLength(50)
  email: string;

  @ApiProperty({ example: "1234", description: "The password of the user" })
  @IsString()
  @IsOptional()
  @MaxLength(50)
  password: string;

  @ApiProperty({ example: "naver", description: "The provider of the user" })
  @IsString()
  @IsNotEmpty()
  @MaxLength(20)
  provider: string;

  @ApiProperty({ example: "test", description: "The nickname of the user" })
  @IsString()
  @IsNotEmpty()
  @MaxLength(20)
  nickName: string;

  @ApiProperty({ example: "1234/56/78", description: "The birth of the user" })
  @IsString()
  @MaxLength(11)
  @IsNotEmpty()
  age: string;

  @ApiProperty({
    example: true,
    description: "The gender of the user. true is male, false is female",
  })
  @IsBoolean()
  @IsNotEmpty()
  isMale: boolean;
}
