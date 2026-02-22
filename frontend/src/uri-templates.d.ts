declare module 'uri-templates' {
	function UriTemplate(template: string): { fill(vars: Record<string, unknown>): string };
	export default UriTemplate;
}
