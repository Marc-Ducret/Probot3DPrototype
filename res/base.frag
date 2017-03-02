#version 330 core

in vec3 points[3];
in vec3 orths[3];

in vec3 vertCol;
in vec3 worldPos;
in vec3 normal;
out vec4 color;

uniform vec3 camPos;

void main() {
	vec3 lightPos = vec3(2, 2, 3);
	vec3 lightDir = normalize(lightPos - worldPos);
	vec3 camDir   = normalize(camPos   - worldPos);

	float factor = 0;

	float diffuse = dot(normal, lightDir);
	diffuse = max(0, diffuse);

	float spec =    -dot(reflect(lightDir, normal), camDir);
	spec = pow(max(0, spec), 64);

	for(int i = 0; i < 3; i ++) {
		if(length(orths[i]) >= .5F && abs(dot(orths[i], worldPos - points[i])) < .04F) {
//			factor += 0.2F;
//			spec *= 2;
		}
	}

	factor += diffuse * .5F + 0.5F + spec * .2F;

	if(color.rgb == vec3(0, 0, 0)) color = vec4(vertCol, 1.0f) * factor;
}
